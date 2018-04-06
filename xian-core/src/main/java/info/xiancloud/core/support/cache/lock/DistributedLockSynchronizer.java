package info.xiancloud.core.support.cache.lock;

import info.xiancloud.core.support.cache.CacheConfigBean;
import info.xiancloud.core.support.cache.CacheService;
import info.xiancloud.core.support.cache.exception.TimeOutException;
import io.reactivex.Completable;
import io.reactivex.Single;

import java.util.concurrent.Callable;

/**
 * redis synchronizer
 *
 * @author john_zero, happyyangyuan
 */
public class DistributedLockSynchronizer {

    public static Completable run(String lockKey, int expireTimeInSecond, Runnable job) throws TimeOutException {
        return run(lockKey, expireTimeInSecond, job, 3);
    }

    public static Completable run(String lockKey, int expireTimeInSecond, Runnable job, int timeOutInSecond) throws TimeOutException {
        return run(CacheService.CACHE_CONFIG_BEAN, lockKey, expireTimeInSecond, job, timeOutInSecond);
    }

    public static Completable run(CacheConfigBean cacheConfigBean, String lockKey, int expireTimeInSecond, Runnable job, int timeOutInSecond) throws TimeOutException {
        return call(cacheConfigBean, lockKey, System.currentTimeMillis(), expireTimeInSecond, () -> {
            job.run();
            return null;
        }, timeOutInSecond).toCompletable();
    }

    public static <T> Single<T> call(String lockKey, int expireTimeInSecond, Callable<T> callable, int timeOutInSecond) {
        return call(lockKey, System.currentTimeMillis(), expireTimeInSecond, callable, timeOutInSecond);
    }

    public static <T> Single<T> call(String lockKey, Object value, int expireTimeInSecond, Callable<T> callable, int timeOutInSecond) {
        return call(CacheService.CACHE_CONFIG_BEAN, lockKey, value, expireTimeInSecond, callable, timeOutInSecond);
    }

    public static <T> Single<T> call(CacheConfigBean cacheConfigBean, String lockKey, int expireTimeInSecond, Callable<T> callable, int timeOutInSecond) {
        return call(cacheConfigBean, lockKey, System.currentTimeMillis(), expireTimeInSecond, callable, timeOutInSecond);
    }

    /**
     * @param cacheConfigBean    cache data source
     * @param lockKey            lock key
     * @param value              value
     * @param expireTimeInSecond lock expire time in seconds
     * @param callable           the callable
     * @param timeOutInSecond    time out in seconds
     * @param <T>                generic type
     * @return callable result
     */
    public static <T> Single<T> call(CacheConfigBean cacheConfigBean, String lockKey, Object value, int expireTimeInSecond, Callable<T> callable, int timeOutInSecond) {
        return DistributedLock
                .lock(cacheConfigBean, lockKey, value, expireTimeInSecond, timeOutInSecond)
                .map(distributedLock -> {
                    try {
                        return callable.call();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    } finally {
                        if (distributedLock != null)
                            distributedLock.unlock(cacheConfigBean);
                    }
                });
    }

}
