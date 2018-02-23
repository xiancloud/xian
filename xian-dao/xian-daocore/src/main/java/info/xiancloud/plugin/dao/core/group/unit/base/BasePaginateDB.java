package info.xiancloud.plugin.dao.core.group.unit.base;

import info.xiancloud.plugin.UnitMeta;
import info.xiancloud.plugin.dao.core.group.unit.DaoUnit;
import info.xiancloud.plugin.dao.core.jdbc.sql.Action;
import info.xiancloud.plugin.dao.core.jdbc.sql.PaginateSelectAction;

/**
 * 公共查询并分页dao unit
 */
abstract public class BasePaginateDB extends DaoUnit {
    @Override
    public String getName() {
        return "BasePaginateDB";
    }

    @Override
    public Action[] getActions() {
        return new Action[]{
                new PaginateSelectAction() {
                }
        };
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("公共查询 dao unit");
    }

}
