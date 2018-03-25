package info.xiancloud.dao.jdbc.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author happyyangyuan
 */
public abstract class CustomSelectAction extends AbstractAction implements ISelect {

    @Override
    protected abstract String sqlPattern(Map map, Connection connection) throws SQLException;

    @Override
    public int resultType() {
        return SELECT_LIST;
    }

    //---------------------------------concrete-------------------------------------------------//
    @Override
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
