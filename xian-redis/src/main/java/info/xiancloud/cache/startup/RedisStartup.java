package info.xiancloud.cache.startup;

import info.xiancloud.cache.redis.distributed_lock.DistributedReentrantLockProcess;
import info.xiancloud.plugin.init.IStartService;
import info.xiancloud.plugin.thread_pool.ThreadPoolManager;

/**
 * @author happyyangyuan
 */
public class RedisStartup implements IStartService {

    @Override
    public boolean startup() {
        ThreadPoolManager.scheduleAtFixedRate(() -> DistributedReentrantLockProcess.monitoring(), 60 * 1000); // 1 min
        return true;
    }

}
