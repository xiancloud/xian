package info.xiancloud.plugin.dao.core.jdbc.sql;

import info.xiancloud.plugin.dao.core.group.unit.DaoUnit;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author happyyangyuan
 */
public abstract class CustomSelectWhereAction extends WhereAction/*SelectAction*/ implements ISelect {

    @Override
    public abstract String sqlTail(DaoUnit daoUnit, Map map, Connection connection);

    @Override
    public int resultType() {
        return SELECT_LIST;
    }

    //-----------------------concrete-----------------------------------//
    final protected Object executeSql(String preparedSql, Object[] sqlParams, Connection connection) throws SQLException {
        return ISelect.executeWhichSql(resultType(), preparedSql, sqlParams, connection);
    }

    @Override
    final public String[] select() {
        return new String[0];
    }

    @Override
    final public String[] fromTable() {
        return new String[0];
    }

}
