package info.xiancloud.cache.service.unit.object;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;

/**
 * TYPE
 *
 * @author John_zero, happyyangyuan
 * http://redisdoc.com/key/type.html
 */
public class CacheTypeUnit implements Unit {
    @Override
    public String getName() {
        return "cacheType";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("TYPE").setDocApi(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", String.class, "", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) throws Exception {
        String key = msg.getArgMap().get("key").toString();
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        String type = Redis.call(cacheConfigBean, jedis -> jedis.type(key));
        handler.handle(UnitResponse.createSuccess(type));
    }

}
