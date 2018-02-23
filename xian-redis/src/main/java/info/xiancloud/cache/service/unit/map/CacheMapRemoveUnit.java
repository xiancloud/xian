package info.xiancloud.cache.service.unit.map;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.support.cache.CacheConfigBean;

/**
 * Map Remove
 *
 * @author John_zero
 */
public class CacheMapRemoveUnit implements Unit {
    @Override
    public String getName() {
        return "cacheMapRemove";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("Map Remove").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", String.class, "缓存的关键字", REQUIRED)
                .add("field", String.class, "Value KEY", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String key = msg.getArgMap().get("key").toString();
        String field = msg.getArgMap().get("field").toString();
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        long length;
        try {
            length = Redis.call(cacheConfigBean, jedis -> jedis.hdel(key, field));
        } catch (Exception e) {
            return UnitResponse.exception(e);
        }
        return UnitResponse.success(length);
    }

}
