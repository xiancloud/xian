package info.xiancloud.dao.jdbc.sql;

import info.xiancloud.core.Unit;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.Pair;
import info.xiancloud.core.util.Reflection;
import info.xiancloud.dao.jdbc.BasicSqlBuilder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 批量插入记录
 * 入参map = { "values" : [{},{},{}]}
 *
 * @author happyyangyuan
 */
public abstract class BatchInsertAction extends AbstractAction implements ISingleTableAction {

    private Pair<String, Object[]> preparedSqlAndValues;

    @Override
    protected UnitResponse check(Unit daoUnit, Map map, Connection connection) {
        if (getValues().size() >= 500) {
            LOG.warn("批量插入的记录过多:" + getValues().size(), new Throwable("本异常的作用是警告开发者批量插入的长度过长，但是它不阻止过长的插入语句执行."));
        }
        return super.check(daoUnit, map, connection);
    }

    private List<Map> getValues() {
        return Reflection.toType(map.get("values"), List.class);
    }

    /**
     * 注意:批量插入不支持本db框架的pattern模式,原因是本action的入参并不是单纯的一个map,而是map列表,这些map内的key都是一样的
     */
    @Override
    protected String getPreparedSQL() throws SQLException {
        if (preparedSql == null) {
            preparedSqlAndValues = BasicSqlBuilder.buildBatchInsertPreparedSQL(table(), getCols(), getValues());
            preparedSql = preparedSqlAndValues.fst;
        }
        return preparedSql;
    }

    @Override
    protected Object[] getSqlParams() throws SQLException {
        if (sqlParams == null) {
            getPreparedSQL();
            sqlParams = preparedSqlAndValues.snd;
        }
        return sqlParams;
    }

    @Override
    protected String getSqlPattern() throws SQLException {
        return sqlPattern(map, connection);
    }

    @Override
    protected String sqlPattern(Map map, Connection connection) throws SQLException {
        LOG.debug("这里只是为了兼容,返回preparedSQL替代之");
        if (sqlPattern == null) {
            sqlPattern = getPreparedSQL();
        }
        return sqlPattern;
    }

    @Override
    protected Object executeSql(String preparedSql, Object[] sqlParams, Connection connection) throws SQLException {
        LOG.debug("返回的是插入的条数");
        List values = Reflection.toType(map.get("values"), List.class);
        if (values == null || values.isEmpty()) {
            LOG.warn("没什么可以插入的数据，什么也不做");
            return 0;
        }
        return ISingleTableAction.dosql(preparedSql, sqlParams, connection);
    }

    private String[] cols;

    public String[] getCols() throws SQLException {
        if (cols == null || cols.length == 0) {
            cols = ISingleTableAction.queryCols(table(), connection);
        }
        return cols;
    }

}
