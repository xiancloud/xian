package info.xiancloud.cache.service.unit.list;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.support.cache.CacheConfigBean;

/**
 * List Exists
 *
 * @author John_zero
 */
public class CacheListExistsUnit implements Unit {

    @Override
    public String getName() {
        return "cacheListExists";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("List Exists").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input().add("key", String.class, "缓存的关键字", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String key = msg.getArgMap().get("key").toString();
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);
        try {
            boolean result = Redis.call(cacheConfigBean, jedis -> jedis.exists(key));
            return UnitResponse.success(result);
        } catch (Exception e) {
            return UnitResponse.exception(e);
        }
    }

}
