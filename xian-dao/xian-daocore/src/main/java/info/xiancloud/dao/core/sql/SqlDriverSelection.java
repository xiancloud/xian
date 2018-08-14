package info.xiancloud.dao.core.sql;

import info.xiancloud.dao.core.model.sqlresult.RecordsListSelectionResult;
import io.reactivex.Single;

import java.util.Map;

/**
 * selection method declaration
 *
 * @author happyyangyuan
 */
public interface SqlDriverSelection {

    /**
     * do the sql query
     *
     * @param patternSql xian pattern sql
     * @param map        parameter map
     * @return deferred selection sql execution result
     */
    Single<RecordsListSelectionResult> select(String patternSql, Map<String, Object> map);

}
