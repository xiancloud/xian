package info.xiancloud.dao.core.action;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Unit;
import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.ArrayUtil;
import info.xiancloud.core.util.LOG;
import info.xiancloud.dao.core.connection.XianConnection;
import info.xiancloud.dao.core.model.sqlresult.SqlExecutionResult;
import info.xiancloud.dao.core.sql.BaseSqlDriver;
import info.xiancloud.dao.core.sql.XianSqlDriver;
import info.xiancloud.dao.core.units.DaoUnit;
import info.xiancloud.dao.core.utils.JdbcPatternUtil;
import info.xiancloud.dao.core.utils.MapFormat;
import info.xiancloud.dao.core.utils.SqlUtils;
import io.reactivex.Single;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Super class of all async actions.
 * Sql actions are non-singleton
 *
 * @author happyyangyuan
 */
public abstract class AbstractSqlAction implements SqlAction, ISqlLogger {

    private String patternSql;
    private String fullSql;
    private Map<String, Object> map;
    private XianConnection connection;
    private DaoUnit daoUnit;
    private String msgId;

    /**
     * stateful sql driver, non-singleton, lazy-init
     */
    private XianSqlDriver sqlDriver;
    /**
     * 模式参数最大长度，用于打印完整sql语句日志时使用；
     * 取值必须小于{@link MapFormat#BUFSIZE}
     */
    private static final int MAX_PATTERN_PARAM_COUNT = 100 < MapFormat.BUFSIZE ? 100 : MapFormat.BUFSIZE;
    private static final String CONFIG_LOG_DETAILED_SQL = "logDetailedSql";
    private static final long SLOW_QUERY_IN_MILLIS = 5 * 1000;

    @Override
    public final Single<UnitResponse> execute(Unit daoUnit, Map<String, Object> map, XianConnection connection, String msgId) {
        ///Not needed anymore because we have make all sql execution on xian thread pool managed thread pool
        // set msgId in order make sure compatibility of synchronous and asynchronous dao.
        // boolean msgIdWritten = MsgIdHolder.set(msgId);
        // try {
        //    ...
        // }
        // finally {
        //     if (msgIdWritten) {
        //         MsgIdHolder.clear();
        //     }
        //}
        this.msgId = msgId;
        this.map = map;
        this.connection = connection;
        this.daoUnit = (DaoUnit) daoUnit;
        sqlDriver = getSqlDriver();
        if (ignore()) {
            return Single.just(UnitResponse.createSuccess(
                    String.format("This sql action '%s.%s' is ignored for execution.",
                            this.daoUnit.getName(), getClass().getSimpleName())
            ));
        } else {
            UnitResponse response = check();
            if (!response.succeeded()) {
                return Single.just(response);
            }
        }
        if (XianConfig.getBoolean(CONFIG_LOG_DETAILED_SQL, true)) {
            logSql(map);
        }
        final long before = System.nanoTime();
        return executeSql()
                .flatMap(sqlExecutionResult -> Single.just(UnitResponse.createSuccess(sqlExecutionResult)))
                .doOnSuccess(unitResponse -> after(before))
                .onErrorReturn(error -> {
                    LOG.error(error);
                    return getSqlDriver().handleException(error, this);
                })
                ;
    }

    /**
     * execute the sql asynchronously and return a single object.
     *
     * @return the deferred sql execution result
     */
    abstract protected Single<? extends SqlExecutionResult> executeSql();

    @Override
    public XianSqlDriver getSqlDriver() {
        if (sqlDriver == null) {
            try {
                sqlDriver = BaseSqlDriver.XIAN_SQL_DRIVER_CLASS.newInstance().setConnection(getConnection());
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return sqlDriver;
    }

    private void after(long before) {
        ///Not needed anymore because we have make all sql execution on xian thread pool managed thread pool
        // set msgId in order make sure compatibility of synchronous and asynchronous dao.
        // boolean msgIdWritten = MsgIdHolder.set(msgId);
        // try {
        //    ...
        // }
        // finally {
        //     if (msgIdWritten) {
        //         MsgIdHolder.clear();
        //     }
        //}
        Long howLong = ((System.nanoTime() - before)) / 1000000;
        JSONObject sqlLog = new JSONObject() {{
            put("type", "sql");
            put("cost", howLong);
            put("sql", patternSql);
            put("description", "执行SQL耗时 ".concat(howLong.toString()).concat(" ms"));
        }};
        if (howLong > SLOW_QUERY_IN_MILLIS) {
            sqlLog.put("description", String.format("超过%sms的慢查询", SLOW_QUERY_IN_MILLIS));
            LOG.error(sqlLog.toJSONString());
        } else {
            LOG.info(sqlLog.toJSONString());
        }
    }

    @Override
    public void logSql(Map<String, Object> map) {
        ///Not needed anymore because we have make all sql execution on xian thread pool managed thread pool
        // set msgId in order make sure compatibility of synchronous and asynchronous dao.
        // boolean msgIdWritten = MsgIdHolder.set(msgId);
        // try {
        //    ...
        // }
        // finally {
        //     if (msgIdWritten) {
        //         MsgIdHolder.clear();
        //     }
        //}
        LOG.info("XianPatternSQL ：" + getPatternSql());
        LOG.info("Prepared SQL：" + getSqlDriver().preparedSql(getPatternSql()));
        LOG.info("Full SQL：" + getFullSql());
    }

    @Override
    public UnitResponse check() {
        return UnitResponse.createSuccess("默认不做校验，如果特殊情况，请在子类重载该校验方法");
    }

    @Override
    public final String getPatternSql() {
        if (patternSql == null) {
            patternSql = adjustInClause(JdbcPatternUtil.bareError(patternSql()));
        }
        return patternSql;
    }

    /**
     * produces a pattern sql.
     * This method is called only once.
     *
     * @return a deferred result which will emit either a single successful pattern or an error
     */
    abstract protected String patternSql();

    public void setConnection(XianConnection connection) {
        this.connection = connection;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    /**
     * If you want to ignore this sql action, please overwirte this method and return true
     *
     * @return true to indicate that the sql not to be executed, false otherwise
     */
    protected boolean ignore() {
        return false;
    }

    private String adjustInClause(String sqlPattern) {
        String regex = " +[i|I][n|N] *\\{[^}]*}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sqlPattern);
        while (matcher.find()) {
            String matched = matcher.group();
            for (String key : JdbcPatternUtil.getCamelKeys(matched)) {
                // 处理集合
                if (getMap().get(key) instanceof Collection) {
                    Collection collection = (Collection) getMap().get(key);
                    getMap().put(key, collection.toArray());
                }
                if (getMap().get(key).getClass().isArray()) {
                    Object[] arrayValue = ArrayUtil.toObjectArray(getMap().get(key));
                    StringBuilder inClause = new StringBuilder(" in ( ");
                    for (int i = 0; i < arrayValue.length; i++) {
                        //特殊符号处理key,避免key冲突,特别需要注意的是,不允许使用下划线'_',因为它与转驼峰处理逻辑有冲突
                        String keyI = key + "@" + i;
                        getMap().put(keyI, arrayValue[i]);
                        inClause.append("{").append(keyI).append("},");
                    }
                    String finalInClause = inClause.substring(0, inClause.length() - 1) + " )";
                    sqlPattern = sqlPattern.replaceFirst(regex, finalInClause);
                }
            }
        }
        return sqlPattern;
    }

    @Override
    public final Map<String, Object> getMap() {
        return map;
    }

    @Override
    public final XianConnection getConnection() {
        return connection;
    }

    @Override
    public final DaoUnit getDaoUnit() {
        return daoUnit;
    }

    @Override
    public final String getFullSql() {
        if (fullSql == null) {
            fullSql = getSqlDriver().preparedParams(getPatternSql(), map).length <= MAX_PATTERN_PARAM_COUNT ?
                    SqlUtils.mapToSql(getPatternSql(), map) :
                    String.format("模式参数太多则忽略完整sql拼接动作以节省开销。实际传入的模式参数个数为%s，上限为%s",
                            getSqlDriver().preparedParams(getPatternSql(), map).length, MAX_PATTERN_PARAM_COUNT);
        }
        return fullSql;
    }
}
