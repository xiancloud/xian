package info.xiancloud.dao.core.sql;

import info.xiancloud.dao.core.model.sqlresult.DeletionResult;
import io.reactivex.Single;

import java.util.Map;

/**
 * interface declaring deleting method which executes deleting sql.
 *
 * @author happyyangyuan
 */
public interface SqlDriverDeletion {

    /**
     * execute the given deletion sql
     *
     * @param patternSql xian pattern sql
     * @param map        parameter map
     * @return deferred deletion result
     */
    Single<DeletionResult> delete(String patternSql, Map<String, Object> map);

}
