package info.xiancloud.cache.service.unit.object;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;

/**
 * cache getter
 *
 * @author John_zero, happyyangyuan
 */
public class CacheObjectGetUnit implements Unit {
    @Override
    public String getName() {
        return "cacheObjectGet";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("缓存获取").setDocApi(false);
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

        Object result = Redis.call(cacheConfigBean, jedis -> jedis.get(key));

        if (result != null && result.toString().equals("nil"))
            result = null;
        handler.handle(UnitResponse.createSuccess(result));
    }

}
