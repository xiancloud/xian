package info.xiancloud.dao.core.units;

import info.xiancloud.core.UnitMeta;
import info.xiancloud.dao.core.action.SqlAction;
import info.xiancloud.dao.core.action.WhereAction;
import info.xiancloud.dao.core.action.select.BaseInternalSelectionAction;
import info.xiancloud.dao.core.model.sqlresult.SqlExecutionResult;
import io.reactivex.Single;

/**
 * 公共查询 dao unit
 */
abstract public class BaseQueryDB extends DaoUnit {
    @Override
    public String getName() {
        return "BaseQueryDB";
    }

    @Override
    public SqlAction[] getActions() {
        return new SqlAction[]{
                new BaseInternalSelectionAction() {
                    @Override
                    protected String sqlHeader() {
                        return null;
                    }

                    @Override
                    protected String[] searchConditions() {
                        return new String[0];
                    }
                }
        };
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("公共查询 dao unit");
    }

}
