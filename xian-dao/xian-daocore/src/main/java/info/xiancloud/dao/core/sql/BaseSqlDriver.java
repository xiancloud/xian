package info.xiancloud.dao.core.sql;

import info.xiancloud.core.util.Reflection;
import info.xiancloud.dao.core.action.insert.BatchInsertAction;
import info.xiancloud.dao.core.connection.XianConnection;
import info.xiancloud.dao.core.utils.JdbcPatternUtil;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * prepared sql builder,executor parent class
 *
 * @author happyyangyuan
 */
public abstract class BaseSqlDriver implements XianSqlDriver {

    protected String preparedSql;
    /**
     * key list in the pattern sql.
     * Pattern keys are not for {@link BatchInsertAction batch insertion action}.
     * Driver for {@link BatchInsertAction batch insertion action} this property is always null.
     */
    protected List<String> patternKeys;
    protected Object[] preparedParams;

    protected XianConnection connection;
    protected String idCol;
    /**
     * xian sql driver class.
     * You should provide one and only one xian sql driver implementation.
     * We choose only one sql driver implementation.
     * With more than one sql driver implementations may cause unpredictable results.
     */
    public static Class<? extends XianSqlDriver> XIAN_SQL_DRIVER_CLASS;

    static {
        try {
            //Here we only choose the first xian sql driver implementation.
            XIAN_SQL_DRIVER_CLASS = Reflection.getNonAbstractSubclasses(XianSqlDriver.class).iterator().next();
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public Object[] preparedParams(String patternSql, Map<String, Object> map) {
        if (preparedParams == null) {
            preparedParams = JdbcPatternUtil.getValues(patternKeys(patternSql), map).toArray();
            for (int i = 0; i < preparedParams.length; i++) {
                if (preparedParams[i] instanceof Calendar) {
                    // 因为prepared statement不支持calendar类型,所以需要将其其中的calendar转为date
                    preparedParams[i] = ((Calendar) preparedParams[i]).getTime();
                }
            }
        }
        return preparedParams;
    }

    @Override
    public List<String> patternKeys(String sqlPattern) {
        if (patternKeys == null) {
            patternKeys = JdbcPatternUtil.getCamelKeys(sqlPattern);
        }
        return patternKeys;
    }

    @Override
    public final XianSqlDriver setConnection(XianConnection connection) {
        this.connection = connection;
        setConnection0(connection);
        return this;
    }

    /**
     * set internal connection0
     *
     * @param connection xian connection
     * @return this
     */
    protected abstract BaseSqlDriver setConnection0(XianConnection connection);
}
