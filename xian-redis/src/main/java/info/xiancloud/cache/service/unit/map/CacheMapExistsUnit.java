package info.xiancloud.cache.service.unit.map;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.redis.operate.MapCacheOperate;
import info.xiancloud.cache.redis.operate.ObjectCacheOperate;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.support.cache.CacheConfigBean;
import redis.clients.jedis.Jedis;

/**
 * Map Exists
 *
 * @author John_zero
 */
public class CacheMapExistsUnit implements Unit {
    @Override
    public String getName() {
        return "cacheMapExists";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("Map Exists").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", String.class, "", REQUIRED)
                .add("field", String.class, "", NOT_REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String key = msg.getArgMap().get("key").toString();
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        boolean exists = false;
        try (Jedis jedis = Redis.useDataSource(cacheConfigBean).getResource()) {
            if (msg.getArgMap().containsKey("field")) {
                String field = msg.getArgMap().get("field").toString();
                exists = MapCacheOperate.exists(jedis, key, field);
            } else {
                exists = ObjectCacheOperate.exists(jedis, key);
            }
        } catch (Exception e) {
            return UnitResponse.exception(e);
        }
        return UnitResponse.success(exists);
    }

}
