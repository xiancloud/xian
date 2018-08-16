package info.xiancloud.dao.core.units;

import info.xiancloud.core.UnitMeta;
import info.xiancloud.dao.core.action.SqlAction;
import info.xiancloud.dao.core.action.select.InternalSelectionAction;

/**
 * 公共查询 dao unit
 *
 * @author happyyangyuan
 * @deprecated todo not fully developed
 */
abstract public class BaseQueryDB extends DaoUnit {
    @Override
    public String getName() {
        return "BaseQueryDB";
    }

    @Override
    public SqlAction[] getActions() {
        return new SqlAction[]{
                /*new InternalSelectionAction() {
                }*/
        };
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("公共查询 dao unit");
    }

}
