package info.xiancloud.redis.distributed_lock;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.support.cache.CacheService;
import info.xiancloud.core.support.cache.lock.DistributedLockSynchronizer;
import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.LOG;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @author John_zero, happyyangyuan
 */
public class DistributedLockTest {

    @Before
    public void initialize() {
        /*
         * 初始化缓存服务
         */
        CacheService.initCacheService(getUrl(), getPassword(), getDBIndex());
    }

    @After
    public void finish() {

    }

    private static String getUrl() {
        if (EnvUtil.isLan())
            return XianConfig.get("redisLanUrl"); // 腾讯云内网内
        else
            return XianConfig.get("redisInternetUrl"); // 外网
    }

    private static String getPassword() {
        return XianConfig.get("redisPassword");
    }

    private static int getDBIndex() {
        return XianConfig.getIntValue("redisDbIndex", 0);
    }

    @Test
    public void synchronizer() {
        CountDownLatch countDownLatch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {
            int _i = i;
            ThreadPoolManager.execute(() -> {
                DistributedLockSynchronizer.call("00000000_" + _i, 1, () -> {
                    LOG.info("各种同步代码");
                    return null;
                }, 3);

                countDownLatch.countDown();
            });
        }

        try {
            countDownLatch.await();
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    @Test
    public void retryAwaitLockBySynchronizer() {
        int number = 10;

        CountDownLatch countDownLatch = new CountDownLatch(number * 5);

        for (int i = 0; i < number; i++) {
            int _i = i;
            for (int j = 0; j < 5; j++) {
                int _j = j;
                ThreadPoolManager.execute(() ->
                {
                    final long startTime = System.currentTimeMillis();

                    DistributedLockSynchronizer.call("lock_name_" + _i, System.currentTimeMillis(), 5, () ->
                    {
                        try {
                            int millis = 500;

//                        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(millis));
//                        Thread.sleep(millis);

                            LOG.info(_i + "_" + _j + " 抢到锁了, 各种同步代码预期耗时: " + millis);
                        } catch (Exception e) {
                            LOG.error(e);
                        }
                        return null;
                    }, 2);

                    final long endTime = System.currentTimeMillis();
                    LOG.warn(_i + "_" + _j + ", 加锁解锁(空实现)耗时: " + (endTime - startTime));

                    countDownLatch.countDown();
                });
            }
        }

        try {
            countDownLatch.await();
        } catch (Exception e) {
            LOG.error(e);
        }

        SingleRxXian.call("diyMonitor", "jedisLockMonitor", new JSONObject());
    }

    @Test
    public void reentrantLockBySynchronizer() {
        DistributedLockSynchronizer.call("reentrant-lock", System.currentTimeMillis(), -1, () ->
        {
            LOG.info("一次分布式锁: start");

            DistributedLockSynchronizer.call("reentrant-lock", System.currentTimeMillis(), 5, () ->
            {
                LOG.info("二次分布式锁: start");
                LOG.info("==================");
                LOG.info("二次分布式锁: end");
                return null;
            }, 2);

            LOG.info("一次分布式锁: end");

            return null;
        }, -1);
    }

    @Test
    public void QPS() {
        final CountDownLatch startCountDownLatch = new CountDownLatch(1);

        final int number = 1000;

        final CountDownLatch finishCountDownLatch = new CountDownLatch(number);

        final List<Long> consumeTimes = new CopyOnWriteArrayList<>();

        for (int i = 0; i < number; i++) {
            int _i = i;
            ThreadPoolManager.execute(() -> {
                try {
                    startCountDownLatch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                long startTime = System.nanoTime();

                // Redis Lock
                DistributedLockSynchronizer.call("QPS_" + _i, 3, () -> {
                    return null;
                }, 3);

                // ZK Lock
//                try
//                {
//                    Synchronizer.call("QPS_" + _i, () -> {
//                        return null;
//                    }, 3L);
//                }
//                catch (Exception e)
//                {
//                    LOG.error(e);
//                }

                finishCountDownLatch.countDown();

                long endTime = System.nanoTime();

                consumeTimes.add(endTime - startTime);
            });
        }

        try {
            startCountDownLatch.countDown();

            finishCountDownLatch.await();

            long totalConsumeTime = 0;
            for (long consumeTime : consumeTimes)
                totalConsumeTime += consumeTime;

            long avgConsumeTime = totalConsumeTime / number;

            LongSummaryStatistics intSummaryStatistics = consumeTimes.stream().collect(Collectors.summarizingLong(value -> value));

            LOG.info(String.format("QPS, 加锁解锁, 任务数量: %s, 累计耗时: %s, %s, 平均耗时：%s, %s, %s", number, totalConsumeTime, totalConsumeTime / 1000000, avgConsumeTime, avgConsumeTime / 1000000, intSummaryStatistics));
        } catch (Exception e) {
            LOG.error(e);
        }

        SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheKeys", new JSONObject() {{
            put("pattern", "LOCK_QPS_*");
        }}).subscribe(unitResponseObject -> {
            if (unitResponseObject.succeeded() && unitResponseObject.getData() != null) {
                Set<String> keys = unitResponseObject.getData();

                LOG.info(String.format("分布锁剩余数量: %s", keys.size()));
            }
            SingleRxXian.call("diyMonitor", "jedisLockMonitor", new JSONObject());
        });

    }

}
