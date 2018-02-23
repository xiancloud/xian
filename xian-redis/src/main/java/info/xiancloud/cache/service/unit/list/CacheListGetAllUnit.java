package info.xiancloud.cache.service.unit.list;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.redis.operate.ListCacheOperate;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.support.cache.CacheConfigBean;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * List GetAll
 *
 * @author John_zero
 */
public class CacheListGetAllUnit implements Unit {
    @Override
    public String getName() {
        return "cacheListGetAll";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("List GetAll").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input().add("key", Object.class, "缓存的关键字", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String key = msg.getArgMap().get("key").toString();
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        List<String> result = null;
        try (Jedis jedis = Redis.useDataSource(cacheConfigBean).getResource()) {
            result = ListCacheOperate.range(jedis, key, 0, -1);
        } catch (Exception e) {
            return UnitResponse.exception(e);
        }
        return UnitResponse.success(result);
    }

}
