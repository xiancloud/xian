package info.xiancloud.cache.service.unit.list;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.redis.operate.ListCacheOperate;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;
import redis.clients.jedis.Jedis;

/**
 * List Remove
 *
 * @author John_zero
 */
public class CacheListRemoveUnit implements Unit {
    @Override
    public String getName() {
        return "cacheListRemove";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("List Remove").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", Object.class, "缓存的关键字", REQUIRED)
                .add("valueObj", Object.class, "", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String key = msg.getArgMap().get("key").toString();
        Object valueObj = msg.getArgMap().get("valueObj");
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        long length = 0;
        try (Jedis jedis = Redis.useDataSource(cacheConfigBean).getResource()) {
            length = ListCacheOperate.remove(jedis, key, valueObj);
        } catch (Exception e) {
            return UnitResponse.exception(e);
        }
        return UnitResponse.success(length);
    }

}
