package info.xiancloud.plugin.dao.core.jdbc.sql.ddl;

import info.xiancloud.plugin.dao.core.jdbc.sql.WhereAction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author happyyangyuan
 */
public abstract class DDLWhereAction extends WhereAction implements IDDL {

    @Override
    protected Object executeSql(String preparedSql, Object[] sqlParams, Connection connection) throws SQLException {
        return IDDL.executeDDL(preparedSql, sqlParams, connection);
    }

}
