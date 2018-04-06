package info.xiancloud.cache.service.unit.map;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.redis.util.FormatUtil;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * Map PutAll
 *
 * @author John_zero, happyyangyuan
 */
public class CacheMapPutAllUnit implements Unit {
    @Override
    public String getName() {
        return "cacheMapPutAll";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("Map PutAll").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", String.class, "缓存的关键字", REQUIRED)
                .add("maps", Map.class, "缓存的内容", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        String key = msg.getArgMap().get("key").toString();
        Map maps = msg.get("maps", Map.class);
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        try (Jedis jedis = Redis.useDataSource(cacheConfigBean).getResource()) {
            if (maps != null && !maps.isEmpty()) {
                Map<String, String> _maps = new HashMap<>();
                maps.forEach((field, valueObj) -> {
                    String value = FormatUtil.formatValue(valueObj);
                    _maps.put(field.toString(), value);
                });

                jedis.hmset(key, _maps);
            }
            handler.handle(UnitResponse.createSuccess());
        } catch (Exception e) {
            handler.handle(UnitResponse.createException(e));
        }
    }

}
