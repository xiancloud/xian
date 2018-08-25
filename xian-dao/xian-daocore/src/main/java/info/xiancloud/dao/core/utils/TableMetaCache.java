package info.xiancloud.dao.core.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import info.xiancloud.core.util.LOG;
import info.xiancloud.dao.core.pool.PoolFactory;
import info.xiancloud.dao.core.sql.BaseSqlDriver;

import java.util.concurrent.TimeUnit;

/**
 * Table meta information cache
 *
 * @author happyyangyuan
 */
public class TableMetaCache {

    /**
     * table and column names cache map
     */
    public static LoadingCache<String, String[]> COLS = CacheBuilder.newBuilder()
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .build(new CacheLoader<String, String[]>() {
                @Override
                public String[] load(String tableName) {
                    LOG.info("Load/refresh columns of table: " + tableName);
                    return PoolFactory.getPool().getSlaveDatasource()
                            .getConnection()
                            .flatMap(connection -> BaseSqlDriver.XIAN_SQL_DRIVER_CLASS.newInstance()
                                    .setConnection(connection)
                                    .queryCols(tableName)
                                    .doFinally(() -> connection.close().subscribe())
                            )
                            .blockingGet();
                }
            });

    /**
     * table primary key cache
     */
    public static LoadingCache<String, String> ID_COL = CacheBuilder.newBuilder()
            .expireAfterWrite(300, TimeUnit.SECONDS)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String tableName) {
                    LOG.info("Load/refresh id column of table: " + tableName);
                    return PoolFactory.getPool().getSlaveDatasource()
                            .getConnection()
                            .flatMap(connection -> BaseSqlDriver.XIAN_SQL_DRIVER_CLASS.newInstance()
                                    .setConnection(connection)
                                    .getIdCol(tableName)
                                    .doFinally(() -> connection.close().subscribe()))
                            .blockingGet();
                }
            });

    /**
     * In order to avoid blocking in pg reactive vert.x context.
     *
     * @param tableName table name
     */
    public static void makeSureCache(String tableName) {
        COLS.getUnchecked(tableName);
        ID_COL.getUnchecked(tableName);
    }
}
