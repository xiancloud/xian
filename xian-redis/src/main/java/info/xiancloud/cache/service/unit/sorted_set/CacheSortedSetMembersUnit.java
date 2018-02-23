package info.xiancloud.cache.service.unit.sorted_set;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.support.cache.CacheConfigBean;

import java.util.Set;

/**
 * Sorted Set Members
 * <p>
 * http://doc.redisfans.com/sorted_set/zrange.html
 *
 * @author John_zero
 */
public class CacheSortedSetMembersUnit implements Unit {

    @Override
    public String getName() {
        return "cacheSortedSetMembers";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("Sorted Set Members").setPublic(false);
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
            Set<String> members = Redis.call(cacheConfigBean, jedis -> jedis.zrange(key, 0, -1));
            return UnitResponse.success(members);
        } catch (Throwable e) {
            return UnitResponse.exception(e);
        }
    }

}
