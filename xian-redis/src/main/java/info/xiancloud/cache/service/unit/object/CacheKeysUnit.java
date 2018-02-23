package info.xiancloud.cache.service.unit.object;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.support.cache.CacheConfigBean;

import java.util.Set;

/**
 * KEYS
 *
 * @author John_zero
 */
public class CacheKeysUnit implements Unit {
    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public String getName() {
        return "cacheKeys";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("KEYS").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input().add("pattern", String.class, "", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String pattern = msg.getArgMap().get("pattern").toString();
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);
        try {
            Set<String> keys = Redis.call(cacheConfigBean, jedis -> jedis.keys(pattern));
            return UnitResponse.success(keys);
        } catch (Exception e) {
            return UnitResponse.exception(e);
        }
    }

}
