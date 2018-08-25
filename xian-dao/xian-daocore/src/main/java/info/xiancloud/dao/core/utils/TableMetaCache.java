package info.xiancloud.dao.core.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import info.xiancloud.core.message.IdManager;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.thread.MsgIdHolder;
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
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build(new CacheLoader<String, String[]>() {
                @Override
                public String[] load(String tableName) throws Exception {
                    LOG.info("fffffffffffffffffffffffff    "+MsgIdHolder.get());
                    LOG.info("Load/refresh columns of table: " + tableName);
                    return BaseSqlDriver.XIAN_SQL_DRIVER_CLASS.newInstance()
                            .setConnection(PoolFactory.getPool().getSlaveDatasource().getConnection().blockingGet())
                            .queryCols(tableName).blockingGet();
                }
            });

    /**
     * table primary key cache
     */
    public static LoadingCache<String, String> ID_COL = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String tableName) throws Exception {
                    LOG.info("fffffffffffffffffffffffff    "+MsgIdHolder.get());
                    LOG.info("Load/refresh id column of table: " + tableName);
                    return BaseSqlDriver.XIAN_SQL_DRIVER_CLASS.newInstance()
                            .setConnection(PoolFactory.getPool().getSlaveDatasource().getConnection().blockingGet())
                            .getIdCol(tableName).blockingGet();
                }
            });
}
