package info.xiancloud.dao.core.sql;

import info.xiancloud.dao.core.model.ddl.Table;
import io.reactivex.Completable;
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

    /**
     * build {@link info.xiancloud.dao.core.model.ddl.Table tableMetaData}
     *
     * @param table
     * @return A deferred result
     */
    Completable buildTableMetaData(Table table);

}
