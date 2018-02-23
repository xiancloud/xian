package info.xiancloud.plugin.dao.core.jdbc.sql.ddl;

import info.xiancloud.plugin.dao.core.jdbc.sql.Action;
import info.xiancloud.plugin.dao.core.group.unit.DaoUnit;
import info.xiancloud.plugin.dao.core.jdbc.sql.dml.IDML;
import info.xiancloud.plugin.dao.core.group.DaoGroup;
import info.xiancloud.plugin.message.UnitResponse;

/**
 * @author happyyangyuan
 */
public class DDLChecker {

    public static UnitResponse check(DaoUnit daoUnit) {
        for (Action action : daoUnit.getLocalActions()) {
            if (action instanceof IDML) {
                return UnitResponse.error(DaoGroup.CODE_DB_ERROR, null, "不允许同时执行DML语句和DDL语句");
            }
        }
        return UnitResponse.success("OK");
    }
}
