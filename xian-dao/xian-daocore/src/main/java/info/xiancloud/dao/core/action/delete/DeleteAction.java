package info.xiancloud.dao.core.action.delete;

import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.dao.core.action.IDML;
import info.xiancloud.dao.core.action.ISingleTableAction;
import info.xiancloud.dao.core.action.SafetyChecker;
import info.xiancloud.dao.core.action.WhereAction;
import info.xiancloud.dao.core.model.sqlresult.DeletionResult;
import io.reactivex.Single;

/**
 * Sql action of deletion
 *
 * @author happyyangyuan
 */
public abstract class DeleteAction extends WhereAction implements ISingleTableAction, IDML {

    @Override
    protected String sqlHeader() {
        return "delete from ".concat(getTableName());
    }

    @Override
    protected Single<DeletionResult> executeSql() {
        return getSqlDriver().delete(getPatternSql(), getMap());
    }

    @Override
    public UnitResponse check() {
        UnitResponse unitResponseObject = super.check();
        if (!unitResponseObject.succeeded()) {
            return unitResponseObject;
        }
        return SafetyChecker.doCheck(this, getMap());
    }
}
