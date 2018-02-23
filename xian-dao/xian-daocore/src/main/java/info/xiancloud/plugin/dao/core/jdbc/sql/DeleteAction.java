package info.xiancloud.plugin.dao.core.jdbc.sql;

import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.dao.core.jdbc.sql.dml.IDML;
import info.xiancloud.plugin.message.UnitResponse;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author happyyangyuan
 */
public abstract class DeleteAction extends WhereAction implements ISingleTableAction, IDML {

    @Override
    protected String sqlHeader(Map map, Connection connection) throws SQLException {
        return "delete from ".concat(table());
    }

    @Override
    protected Object executeSql(String preparedSql, Object[] sqlParams, Connection connection) throws SQLException {
        return ISingleTableAction.dosql(preparedSql, sqlParams, connection);
    }

    private String[] cols;

    public String[] getCols() throws SQLException {
        if (cols == null || cols.length == 0) {
            cols = ISingleTableAction.queryCols(table(), connection);
        }
        return cols;
    }

    protected UnitResponse check(Unit daoUnit, Map map, Connection connection) {
        UnitResponse unitResponse = super.check(daoUnit, map, connection);
        if (!unitResponse.getCode().equals(Group.CODE_SUCCESS)) {
            return unitResponse;
        }
        return SafetyChecker.doCheck(this, map);
    }
}
