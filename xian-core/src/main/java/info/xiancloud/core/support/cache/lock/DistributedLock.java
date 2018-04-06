package info.xiancloud.core.support.cache.lock;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Group;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;
import info.xiancloud.core.support.cache.exception.TimeOutException;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.LOG;
import io.reactivex.Single;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * distributed lock using redis
 */
public class DistributedLock {
    private static final AtomicInteger AUTO_INCREMENT = new AtomicInteger(0);

    private final int autoIncrement;
    private final String key;
    private final String lockKey;
    private final Object value;

    public DistributedLock(int autoIncrement, String key, String lockKey, Object value) {
        this.autoIncrement = autoIncrement;
        this.key = key;
        this.lockKey = lockKey;
        this.value = value;
    }

    /**
     * @param cacheConfigBean    cacheConfigBean
     * @param key                key
     * @param value              value
     * @param expireTimeInSecond 单位: 秒, KEY 过期时间
     * @param timeOutInSecond    单位: 秒, 获取锁超时时间
     * @return DistributedLock
     */
    public static Single<DistributedLock> lock(CacheConfigBean cacheConfigBean, String key, Object value, int expireTimeInSecond, int timeOutInSecond) {
        final long applyTime = System.currentTimeMillis();

        final int _expireTimeInSecond = expireTimeInSecond < 1 ? 3 : expireTimeInSecond;
        final int _timeOutInSecond = timeOutInSecond < 1 ? 3 : timeOutInSecond;

        if (expireTimeInSecond != _expireTimeInSecond)
            LOG.warn(String.format("key: %s, 原 expireTime: %s < 1, 校正为现 expireTime: %s", key, expireTimeInSecond, _expireTimeInSecond));
        if (timeOutInSecond != _timeOutInSecond)
            LOG.warn(String.format("key: %s, 原 timeOutInSecond: %s < 1, 校正为现 timeOutInSecond: %s", key, timeOutInSecond, _timeOutInSecond));

        final String lockKey = "LOCK_" + key;

        Single<UnitResponse> single = SingleRxXian.call("cache", "distributedLock", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", lockKey);
            put("value", value);
            put("expireTimeInSecond", _expireTimeInSecond);
            put("timeOutInSecond", _timeOutInSecond);
        }});

        final long receiveTime = System.currentTimeMillis();

        return single.map(unitResponseObject -> {
            if (unitResponseObject.succeeded()) {
                int autoIncrement = AUTO_INCREMENT.incrementAndGet();

                if (!EnvUtil.getEnv().equals(EnvUtil.PRODUCTION))
                    LOG.info(String.format("锁编号: %s, key: %s, lockKey: %s, value: %s, 分布式加锁, 成功, 耗时: %s 毫秒", autoIncrement, key, lockKey, value, (receiveTime - applyTime)));

                return new DistributedLock(autoIncrement, key, lockKey, value);
            } else if (unitResponseObject.getCode().equals(Group.CODE_TIME_OUT))
                throw new TimeOutException(String.format("分布式加锁, 超时, key: %s, lockKey: %s, 耗时: %s 毫秒", key, lockKey, (receiveTime - applyTime)));
            else
                throw new RuntimeException(String.format("分布式加锁, 异常, key: %s, lockKey: %s, 耗时: %s 毫秒", key, lockKey, (receiveTime - applyTime)));
        });
    }

    public Single<Boolean> unlock(CacheConfigBean cacheConfigBean) {
        final long applyTime = System.currentTimeMillis();

        if (lockKey == null)
            return Single.just(false);

        Single<UnitResponse> single = SingleRxXian.call("cache", "distributedUnLock", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", lockKey);
            put("value", value);
        }});

        return single.map(unitResponseObject -> {
            if (!EnvUtil.getEnv().equals(EnvUtil.PRODUCTION)) {
                final long receiveTime = System.currentTimeMillis();
                final String result = unitResponseObject.succeeded() ? "成功" : "失败";
                LOG.info(String.format("锁编号: %s, key: %s, lockKey: %s, value: %s, 分布式解锁, %s, 影响数量: %s, 耗时: %s 毫秒", autoIncrement, key, lockKey, value, result, unitResponseObject.getData(), (receiveTime - applyTime)));
            }

            if (!unitResponseObject.succeeded()) {
                LOG.error(unitResponseObject);
                return false;
            }

            return true;
        });
    }

}
