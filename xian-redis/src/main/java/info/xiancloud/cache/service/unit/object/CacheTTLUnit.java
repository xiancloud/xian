package info.xiancloud.cache.service.unit.object;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;

/**
 * TTL, time to live in seconds
 * <p>
 * http://redisdoc.com/key/ttl.html
 *
 * @author John_zero, happyyangyuan
 */
public class CacheTTLUnit implements Unit {
    @Override
    public String getName() {
        return "cacheTtl";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("TTL").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", String.class, "", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        String key = msg.getArgMap().get("key").toString();
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);
        try {
            long ttl = Redis.call(cacheConfigBean, jedis -> jedis.ttl(key));
            handler.handle(UnitResponse.createSuccess(ttl));
        } catch (Exception e) {
            handler.handle(UnitResponse.createException(e));
        }
    }

}
