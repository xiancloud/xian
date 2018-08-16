package info.xiancloud.dao.core.units;

import info.xiancloud.core.UnitMeta;
import info.xiancloud.dao.core.action.SqlAction;

/**
 * 公共查询并分页dao unit
 *
 * @author happyyangyuan
 * @deprecated todo not fully developed
 */
abstract public class BasePaginateDB extends DaoUnit {
    @Override
    public String getName() {
        return "BasePaginateDB";
    }

    @Override
    public SqlAction[] getActions() {
        return new SqlAction[]{
                /*new PaginateSelectAction() {
                }*/
        };
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("公共查询 dao unit");
    }

}
