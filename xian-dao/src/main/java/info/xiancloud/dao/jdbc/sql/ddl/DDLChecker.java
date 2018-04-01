package info.xiancloud.dao.jdbc.sql.ddl;

import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.dao.group.DaoGroup;
import info.xiancloud.dao.group.unit.DaoUnit;
import info.xiancloud.dao.jdbc.sql.dml.IDML;
import info.xiancloud.dao.jdbc.sql.Action;

/**
 * @author happyyangyuan
 */
public class DDLChecker {

    public static UnitResponse check(DaoUnit daoUnit) {
        for (Action action : daoUnit.getLocalActions()) {
            if (action instanceof IDML) {
                return UnitResponse.createError(DaoGroup.CODE_DB_ERROR, null, "不允许同时执行DML语句和DDL语句");
            }
        }
        return UnitResponse.createSuccess("OK");
    }
}
