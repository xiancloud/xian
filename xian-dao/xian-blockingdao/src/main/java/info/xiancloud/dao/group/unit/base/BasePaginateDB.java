package info.xiancloud.dao.group.unit.base;

import info.xiancloud.core.UnitMeta;
import info.xiancloud.dao.group.unit.DaoUnit;
import info.xiancloud.dao.jdbc.sql.Action;
import info.xiancloud.dao.core.action.select.PaginateSelectAction;

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
        return UnitMeta.createWithDescription("公共查询 dao unit");
    }

}
