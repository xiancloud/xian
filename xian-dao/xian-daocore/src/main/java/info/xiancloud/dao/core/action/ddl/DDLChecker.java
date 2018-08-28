package info.xiancloud.dao.core.action.ddl;

import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.dao.core.DaoGroup;
import info.xiancloud.dao.core.action.IDML;
import info.xiancloud.dao.core.action.SqlAction;
import info.xiancloud.dao.core.units.DaoUnit;

/**
 * Check ddl
 *
 * @author happyyangyuan
 * @deprecated Not used by now.
 */
public class DDLChecker {

    public static UnitResponse check(DaoUnit daoUnit, SqlAction[] actions) {
        for (SqlAction action : actions) {
            if (action instanceof IDML) {
                return UnitResponse.createError(DaoGroup.CODE_DB_ERROR, null, "不允许同时执行DML语句和DDL语句");
            }
        }
        return UnitResponse.createSuccess("OK");
    }
}
