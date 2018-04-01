package info.xiancloud.cache.service.unit.sorted_set;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;

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
            return UnitResponse.createSuccess(length);
        } catch (Throwable e) {
            return UnitResponse.createException(e);
        }
    }

}
