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
 * 分布式 解锁
 *
 * @author John_zero
 */
public class DistributedUnLockUnit implements Unit {
    @Override
    public String getName() {
        return "distributedUnLock";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("分布式 解锁").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", String.class, "缓存的关键字", REQUIRED)
                .add("value", Object.class, "缓存的内容", NOT_REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED)
                ;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String key = msg.getArgMap().get("key").toString();
        Object valueObj = msg.get("value", Object.class, "Distributed Lock");
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        long result = 0;
        try {
            result = DistributedReentrantLockProcess.unLock(cacheConfigBean, key, valueObj);
        } catch (Exception e) {
            DistributedReentrantLockProcess.unLockFailure();
            return UnitResponse.createException(e);
        }
        DistributedReentrantLockProcess.unLockSuccess();
        return UnitResponse.createSuccess(result);
    }

}
