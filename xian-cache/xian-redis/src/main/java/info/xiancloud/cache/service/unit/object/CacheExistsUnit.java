package info.xiancloud.cache.service.unit.object;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;

/**
 * 查询缓存是否存在
 *
 * @author John_zero, happyyangyuan
 */
public class CacheExistsUnit implements Unit {
    @Override
    public String getName() {
        return "cacheExists";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("查询缓存是否存在").setDocApi(false);
    }

    @Override
    public Input getInput() {
        return new Input().add("key", String.class, "缓存的关键字", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) throws Exception {
        String key = msg.getArgMap().get("key").toString();
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);
        boolean result = Redis.call(cacheConfigBean, jedis -> jedis.exists(key));
        handler.handle(UnitResponse.createSuccess(result));
    }

}
