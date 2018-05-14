package info.xiancloud.cache.service.unit.map;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;

import java.util.Set;

/**
 * Map Keys
 *
 * @author John_zero, happyyangyuan
 */
public class CacheMapKeysUnit implements Unit {
    @Override
    public String getName() {
        return "cacheMapKeys";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("Map Keys").setDocApi(false);
    }

    @Override
    public Input getInput() {
        return new Input().add("key", Object.class, "缓存的关键字", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) throws Exception {
        String key = msg.getArgMap().get("key").toString();
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);
        Set<String> keys = Redis.call(cacheConfigBean, jedis -> jedis.hkeys(key));
        handler.handle(UnitResponse.createSuccess(keys));
    }

}
