package info.xiancloud.dao.core.sql;

import info.xiancloud.dao.core.model.sqlresult.UpdatingResult;
import io.reactivex.Single;

import java.util.Map;

/**
 * ability of updating
 *
 * @author happyyangyuan
 */
public interface SalDriverUpdating extends ISingleTableSqlDriver {

    /**
     * do an updating on this connection
     *
     * @param sqlPattern the xian sql pattern
     * @param map        parameters
     * @return the updating result
     */
    Single<UpdatingResult> update(String sqlPattern, Map<String, Object> map);

}
