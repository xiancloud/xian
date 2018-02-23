package info.xiancloud.plugin.support.cache.lock;

import info.xiancloud.plugin.support.cache.CacheConfigBean;
import info.xiancloud.plugin.support.cache.CacheService;
import info.xiancloud.plugin.support.cache.exception.TimeOutException;

import java.util.concurrent.Callable;

public class DistributedLockSynchronizer
{

    public static void run (String lockKey, int expireTimeInSecond, Runnable job) throws TimeOutException
    {
        run(lockKey, expireTimeInSecond, job, 3);
    }

    public static void run (String lockKey, int expireTimeInSecond, Runnable job, int timeOutInSecond) throws TimeOutException
    {
        run(CacheService.CACHE_CONFIG_BEAN, lockKey, expireTimeInSecond, job, timeOutInSecond);
    }

    public static void run (CacheConfigBean cacheConfigBean, String lockKey, int expireTimeInSecond, Runnable job, int timeOutInSecond) throws TimeOutException
    {
        call(cacheConfigBean, lockKey, System.currentTimeMillis(), expireTimeInSecond, () -> {
            job.run();
            return null;
        }, timeOutInSecond);
    }

    public static <T> T call (String lockKey, int expireTimeInSecond, Callable<T> callable, int timeOutInSecond) throws TimeOutException
    {
        return call(lockKey, System.currentTimeMillis(), expireTimeInSecond, callable, timeOutInSecond);
    }

    public static <T> T call (String lockKey, Object value, int expireTimeInSecond, Callable<T> callable, int timeOutInSecond) throws TimeOutException
    {
        return call(CacheService.CACHE_CONFIG_BEAN, lockKey, value, expireTimeInSecond, callable, timeOutInSecond);
    }

    public static <T> T call (CacheConfigBean cacheConfigBean, String lockKey, int expireTimeInSecond, Callable<T> callable, int timeOutInSecond) throws TimeOutException
    {
        return call(cacheConfigBean, lockKey, System.currentTimeMillis(), expireTimeInSecond, callable, timeOutInSecond);
    }

    /**
     * @param cacheConfigBean
     * @param lockKey
     * @param value
     * @param expireTimeInSecond
     * @param callable
     * @param timeOutInSecond
     * @param <T>
     * @return
     * @throws TimeOutException
     */
    public static <T> T call (CacheConfigBean cacheConfigBean, String lockKey, Object value, int expireTimeInSecond, Callable<T> callable, int timeOutInSecond) throws TimeOutException
    {
        DistributedLock distributedLock = DistributedLock.lock(cacheConfigBean, lockKey, value, expireTimeInSecond, timeOutInSecond);
        try
        {
            return callable.call();
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            if(distributedLock != null)
                distributedLock.unlock(cacheConfigBean);
        }
    }

}
