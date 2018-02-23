package info.xiancloud.cache.service.unit.sorted_set;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.support.cache.CacheConfigBean;

/**
 * Sorted Set Length
 * <p>
 * http://doc.redisfans.com/sorted_set/zcard.html
 *
 * @author John_zero
 */
public class CacheSortedSetLengthUnit implements Unit {
    @Override
    public String getName() {
        return "cacheSortedSetLength";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("Sorted Set Length").setPublic(false);
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
            Long length = Redis.call(cacheConfigBean, jedis -> jedis.zcard(key));
            return UnitResponse.success(length);
        } catch (Throwable e) {
            return UnitResponse.exception(e);
        }
    }

}
