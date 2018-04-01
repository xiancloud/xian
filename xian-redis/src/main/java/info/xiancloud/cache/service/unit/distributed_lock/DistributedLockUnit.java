package info.xiancloud.cache.service.unit.distributed_lock;

import info.xiancloud.cache.redis.distributed_lock.DistributedReentrantLockProcess;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;

/**
 * 分布式 加锁
 * <p>
 * http://doc.redisfans.com/string/set.html
 *
 * @author John_zero
 */
public class DistributedLockUnit implements Unit {
    @Override
    public String getName() {
        return "distributedLock";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("分布式 加锁").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", String.class, "缓存的关键字", REQUIRED)
                .add("value", Object.class, "缓存的内容", NOT_REQUIRED)
                .add("expireTimeInSecond", int.class, "设置键的过期时间, 默认: 3秒", NOT_REQUIRED)
                .add("timeOutInSecond", long.class, "获取键的超时时间, 默认: 3秒", NOT_REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String key = msg.getArgMap().get("key").toString();
        Object valueObj = msg.get("value", Object.class, "Distributed Lock");
        int expireTimeInSecond = msg.get("expireTimeInSecond", int.class, 3);
        long timeOutInSecond = msg.get("timeOutInSecond", long.class, 3L);
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);
        boolean isLock = DistributedReentrantLockProcess.lock(cacheConfigBean, key, valueObj, expireTimeInSecond, timeOutInSecond);
        if (isLock) {
            DistributedReentrantLockProcess.lockSuccess();
            return UnitResponse.createSuccess();
        } else {
            DistributedReentrantLockProcess.lockFailure();
            return UnitResponse.createError(CacheGroup.CODE_TIME_OUT, null, null);
        }
    }

}
