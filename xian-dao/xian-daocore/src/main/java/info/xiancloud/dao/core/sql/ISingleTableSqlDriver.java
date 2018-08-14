package info.xiancloud.dao.core.sql;

import io.reactivex.Single;

/**
 * single table operation
 *
 * @author happyyangyuan
 */
public interface ISingleTableSqlDriver {

    /**
     * query column names
     *
     * @param tableName table name
     * @return column name array single object.
     */
    Single<String[]> queryCols(String tableName);

    /**
     * query for the primary column
     *
     * @return primary column name
     */
    Single<String> getIdCol(String tableName);

}
