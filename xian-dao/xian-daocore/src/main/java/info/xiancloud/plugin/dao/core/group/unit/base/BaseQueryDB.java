package info.xiancloud.plugin.dao.core.group.unit.base;

import info.xiancloud.plugin.UnitMeta;
import info.xiancloud.plugin.dao.core.group.unit.DaoUnit;
import info.xiancloud.plugin.dao.core.jdbc.sql.Action;
import info.xiancloud.plugin.dao.core.jdbc.sql.QueryAction;

/**
 * 公共查询 dao unit
 */
abstract public class BaseQueryDB extends DaoUnit {
    @Override
    public String getName() {
        return "BaseQueryDB";
    }

    @Override
    public Action[] getActions() {
        return new Action[]{
                new QueryAction() {
                }
        };
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("公共查询 dao unit");
    }

}
