package info.xiancloud.cache.service.unit.object;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;

import java.util.Set;

/**
 * KEYS
 *
 * @author John_zero, happyyangyuan
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
        return UnitMeta.create().setDescription("KEYS").setDocApi(false);
    }

    @Override
    public Input getInput() {
        return new Input().add("pattern", String.class, "", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) throws Exception {
        String pattern = msg.getArgMap().get("pattern").toString();
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);
        Set<String> keys = Redis.call(cacheConfigBean, jedis -> jedis.keys(pattern));
        handler.handle(UnitResponse.createSuccess(keys));
    }

}
