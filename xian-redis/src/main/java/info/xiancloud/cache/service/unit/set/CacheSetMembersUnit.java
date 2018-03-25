package info.xiancloud.cache.service.unit.set;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;

import java.util.Set;

/**
 * Set SMEMBERS
 *
 * @author John_zero
 */
public class CacheSetMembersUnit implements Unit {
    @Override
    public String getName() {
        return "cacheSetMembers";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("Set SMEMBERS").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", String.class, "", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String key = msg.get("key", String.class);
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        try {
            Set<String> values = Redis.call(cacheConfigBean, jedis -> jedis.smembers(key));
            return UnitResponse.success(values);
        } catch (Throwable e) {
            return UnitResponse.exception(e);
        }
    }

}
