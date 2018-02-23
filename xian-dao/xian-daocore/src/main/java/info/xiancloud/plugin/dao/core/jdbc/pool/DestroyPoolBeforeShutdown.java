package info.xiancloud.plugin.dao.core.jdbc.pool;

import info.xiancloud.plugin.dao.core.jdbc.pool.PoolFactory;
import info.xiancloud.plugin.init.shutdown.ShutdownHook;
import info.xiancloud.plugin.util.LOG;

/**
 * @author happyyangyuan
 */
public class DestroyPoolBeforeShutdown implements ShutdownHook {
    @Override
    public boolean shutdown() {
        try {
            PoolFactory.getPool().destroyPool();
        } catch (Throwable e) {
            LOG.warn("注销数据库连接池出错", e);
            return false;
        }
        return true;
    }
}
