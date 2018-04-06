package info.xiancloud.cache.service.unit.sorted_set;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;

/**
 * Sorted Set Length
 * <p>
 * http://doc.redisfans.com/sorted_set/zcard.html
 *
 * @author John_zero, happyyangyuan
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
        return UnitMeta.createWithDescription("Sorted Set Length").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", String.class, "", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        String key = msg.get("key", String.class);
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        try {
            Long length = Redis.call(cacheConfigBean, jedis -> jedis.zcard(key));
            handler.handle(UnitResponse.createSuccess(length));
        } catch (Throwable e) {
            handler.handle(UnitResponse.createException(e));
        }
    }

}
