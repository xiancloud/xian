package info.xiancloud.dao.core.action.select;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.util.LOG;
import info.xiancloud.dao.core.action.SqlAction;
import info.xiancloud.dao.core.model.sqlresult.ISelectionResult;
import info.xiancloud.dao.core.model.sqlresult.SelectionCountResult;
import info.xiancloud.dao.core.model.sqlresult.SingleRecordSelectionResult;
import io.reactivex.Single;

/**
 * selection action helper class
 *
 * @author happyyangyuan
 */
public abstract class SelectActionHelper {

    public static Single<? extends ISelectionResult> executeWhichSql(SqlAction action, int resultType) {
        if (resultType == ISelect.SELECT_LIST) {
            return action.getSqlDriver().select(action.getPatternSql(), action.getMap());
        }
        if (resultType == ISelect.SELECT_COUNT) {
            return selectCount(action);
        }
        if (resultType == ISelect.SELECT_SINGLE) {
            return selectSingleResult(action);
        }
        throw new RuntimeException("Query result type not supported:" + resultType);
    }

    private static Single<SelectionCountResult> selectCount(SqlAction action) {
        return action.getSqlDriver().select(action.getPatternSql(), action.getMap())
                .map(selectionSqlResult ->
                        new SelectionCountResult().setCount(
                                (Long) selectionSqlResult.getRecords().get(0).values().iterator().next()
                        ));
    }

    private static Single<SingleRecordSelectionResult> selectSingleResult(SqlAction action) {
        return action.getSqlDriver().select(action.getPatternSql(), action.getMap())
                .map(selectionSqlResult -> {
                    if (selectionSqlResult.getCount() == 0) {
                        LOG.warn(new JSONObject().fluentPut("description", "Query result is null")
                                .fluentPut("sql", action.getFullSql()));
                        return new SingleRecordSelectionResult().setCount(0).setRecord(null);
                    } else {
                        if (selectionSqlResult.getCount() > 1) {
                            LOG.warn(new JSONObject()
                                    .fluentPut("description", "Query result records count is more than 1")
                                    .fluentPut("sql", action.getFullSql())
                            );
                        }
                        return new SingleRecordSelectionResult().setRecord(selectionSqlResult.getRecords().get(0))
                                .setCount(selectionSqlResult.getCount());
                    }
                });
    }

}
