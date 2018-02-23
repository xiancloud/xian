package info.xiancloud.plugin.dao.core.jdbc.sql.ddl;

import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.dao.core.group.unit.DaoUnit;
import info.xiancloud.plugin.dao.core.jdbc.sql.AbstractAction;
import info.xiancloud.plugin.message.UnitResponse;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author happyyangyuan
 */
public abstract class DDLAction extends AbstractAction implements IDDL {
    @Override
    protected Object executeSql(String preparedSql, Object[] sqlParams, Connection connection) throws SQLException {
        return IDDL.executeDDL(preparedSql, sqlParams, connection);
    }

    @Override
    protected UnitResponse check(Unit daoUnit, Map map, Connection connection) {
        UnitResponse unitResponseObject = super.check(daoUnit, map, connection);
        if (!unitResponseObject.getCode().equals(Group.CODE_SUCCESS)) {
            return unitResponseObject;
        }
        DaoUnit daounit = (DaoUnit) daoUnit;
        return DDLChecker.check(daounit);
    }
}
