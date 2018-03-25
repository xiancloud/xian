package info.xiancloud.cache.redis.distributed_lock;

import info.xiancloud.cache.redis.Cache;
import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.redis.operate.ObjectCacheOperate;
import info.xiancloud.cache.redis.util.FormatUtil;
import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.support.cache.CacheConfigBean;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.LOG;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * @author John Zero
 */
public class DistributedReentrantLockProcess {

    private final static ThreadLocal<LockData> LOCK_THREAD_DATA = new ThreadLocal<>();

    private static class LockData {
        private final String key;
        private final String value;
        private final AtomicInteger lockCount = new AtomicInteger(1);

        public LockData(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public AtomicInteger getLockCount() {
            return lockCount;
        }
    }

    // 重试次数
    private static final int TRY_NUM = 10;

    /**
     * 加锁
     *
     * @param cacheConfigBean
     * @param key
     * @param valueObj
     * @param expireTimeInSecond
     * @param timeOutInSecond
     * @return
     */
    public static boolean lock(CacheConfigBean cacheConfigBean, String key, Object valueObj, int expireTimeInSecond, long timeOutInSecond) {
        final LockData lockData = LOCK_THREAD_DATA.get();
        if (lockData != null) {
            lockData.getLockCount().incrementAndGet();
            return true;
        }

        long startNano = System.nanoTime();

        // 重试间隔等待时间
        final int retryAwait = (int) timeOutInSecond * 1000 / TRY_NUM;

        int autoIncrementTryNum = 0;
        do {
            autoIncrementTryNum++;

            String result = "nil";
            try (Jedis jedis = Redis.useDataSource(cacheConfigBean).getResource()) {
                if (jedis != null)
                    result = ObjectCacheOperate.set(jedis, key, valueObj, "EX", expireTimeInSecond, "NX");
            } catch (Exception e) {
                LOG.error(e);
            }

            if (!EnvUtil.getEnv().equals(EnvUtil.PRODUCTION))
                LOG.info(String.format("SET %s \"%s\" EX %s NX = result: %s, 第 %s 次尝试", key, valueObj, expireTimeInSecond, result, autoIncrementTryNum));

            if ("OK".equals(result)) {
                LOCK_THREAD_DATA.set(new LockData(key, FormatUtil.formatValue(valueObj)));

                if (!EnvUtil.getEnv().equals(EnvUtil.PRODUCTION)) {
                    long endNano = System.nanoTime();
                    long consumeTime = (endNano - startNano) / 1000000;
                    LOG.info(String.format("key: %s, 分布式加锁, 尝试次数： %s, 累计耗时: %s 纳秒", key, autoIncrementTryNum, consumeTime));
                }

                return true;
            }

            if (!EnvUtil.getEnv().equals(EnvUtil.PRODUCTION))
                LOG.info(String.format("key: %s, 分布式加锁, 目前尝试次数: %s, 暂停: %s 毫秒后继续尝试", key, autoIncrementTryNum, retryAwait));

            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(retryAwait));

        } while (autoIncrementTryNum < TRY_NUM);

        return false;
    }

    /**
     * 解锁
     *
     * @param cacheConfigBean
     * @param key
     * @param valueObj
     * @return
     * @throws Exception
     */
    public static long unLock(CacheConfigBean cacheConfigBean, String key, Object valueObj) throws Exception {
        final LockData lockData = LOCK_THREAD_DATA.get();
        if (lockData == null)
            throw new IllegalMonitorStateException("You do not own the lock: " + key);

        long startNano = System.nanoTime();

        final int newLockCount = lockData.getLockCount().decrementAndGet();
        if (newLockCount > 0) {
            return 1;
        } else if (newLockCount < 0) {
            throw new IllegalMonitorStateException("Lock count has gone negative for lock: " + key);
        } else {
            try (Jedis jedis = Redis.useDataSource(cacheConfigBean).getResource()) {
                final String _value = ObjectCacheOperate.get(jedis, key);
                if (_value == null) {
                    LOG.warn(String.format("key: %s, -1 (没有对应的缓存)", key));
                    return -1;
                }
                final String value = FormatUtil.formatValue(valueObj);
                if (!_value.equals(value)) {
                    LOG.warn(String.format("key: %s, (_value: %s) != (value: %s), -2 (入参值 和 缓存值 不匹配)", key, (_value == null ? "null" : _value), (value == null ? "null" : value)));
                    return -2;
                }

                final long result = ObjectCacheOperate.del(jedis, key);

                if (!EnvUtil.getEnv().equals(EnvUtil.PRODUCTION)) {
                    long endNano = System.nanoTime();
                    LOG.info(String.format("key: %s, 分布式解锁, 累计耗时: %s 纳秒", key, (endNano - startNano) / 1000000));
                    LOG.info(String.format("DEL key: %s = result: %s", key, result));
                }

                return result;
            } catch (Exception e) {
                LOG.error(e);
                return -3;
            } finally {
                LOCK_THREAD_DATA.remove();
            }
        }
    }


    /**
     * 监控部分
     */

    private static final Object LOCK_LOCK = new Object();
    private static final Object UNLOCK_LOCK = new Object();
    private static final AtomicInteger LOCK_SUCCESS = new AtomicInteger();
    private static final AtomicInteger LOCK_FAILURE = new AtomicInteger();
    private static final AtomicInteger UNLOCK_SUCCESS = new AtomicInteger();
    private static final AtomicInteger UNLOCK_FAILURE = new AtomicInteger();

    public static void lockSuccess() {
        synchronized (LOCK_LOCK) {
            LOCK_SUCCESS.incrementAndGet();
        }
    }

    public static void lockFailure() {
        synchronized (LOCK_LOCK) {
            LOCK_FAILURE.incrementAndGet();
        }
    }

    public static void unLockSuccess() {
        synchronized (UNLOCK_LOCK) {
            UNLOCK_SUCCESS.incrementAndGet();
        }
    }

    public static void unLockFailure() {
        synchronized (UNLOCK_LOCK) {
            UNLOCK_FAILURE.incrementAndGet();
        }
    }

    public static int getLockSuccess() {
        synchronized (LOCK_LOCK) {
            int lockSuccess = LOCK_SUCCESS.get();
            LOCK_SUCCESS.set(0);
            return lockSuccess;
        }
    }

    public static int getLockFailure() {
        synchronized (LOCK_LOCK) {
            int lockFailure = LOCK_FAILURE.get();
            LOCK_FAILURE.set(0);
            return lockFailure;
        }
    }

    public static int getUnLockSuccess() {
        synchronized (UNLOCK_LOCK) {
            int unLockSuccess = UNLOCK_SUCCESS.get();
            UNLOCK_SUCCESS.set(0);
            return unLockSuccess;
        }
    }

    public static int getUnLockFailure() {
        synchronized (UNLOCK_LOCK) {
            int unLockFailure = UNLOCK_FAILURE.get();
            UNLOCK_FAILURE.set(0);
            return unLockFailure;
        }
    }

    public static void monitoring() {
        if (!XianConfig.getBoolValue("monitoring.distributedReentrantLock", false))
            return;

        Map<String, Cache> CACHE = Redis.unmodifiableCache();
        if (CACHE == null || CACHE.isEmpty())
            return;

        int lockSuccess = DistributedReentrantLockProcess.getLockSuccess();
        int lockFailure = DistributedReentrantLockProcess.getLockFailure();
        int unLockSuccess = DistributedReentrantLockProcess.getUnLockSuccess();
        int unLockFailure = DistributedReentrantLockProcess.getUnLockFailure();

        LOG.info(String.format("Redis 锁监控: 加锁(成功: %s, 失败: %s), 解锁(成功: %s, 失败: %s)", lockSuccess, lockFailure, unLockSuccess, unLockFailure));
    }

}
