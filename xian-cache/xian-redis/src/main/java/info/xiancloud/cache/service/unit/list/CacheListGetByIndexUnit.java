package info.xiancloud.cache.service.unit.list;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;

/**
 * List Get By Index
 *
 * @author John_zero, happyyangyuan
 */
public class CacheListGetByIndexUnit implements Unit {
    @Override
    public String getName() {
        return "cacheListGetByIndex";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("List Get By Index").setDocApi(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", Object.class, "缓存的关键字", REQUIRED)
                .add("index", Long.class, "下标, 默认: 0", NOT_REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) throws Exception {
        String key = msg.getArgMap().get("key").toString();
        Long index = msg.getArgMap().get("index") != null ? Long.parseLong(msg.getArgMap().get("index").toString()) : 0;
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        String element;
        element = Redis.call(cacheConfigBean, (jedis) -> jedis.lindex(key, index));

        if (element != null && element.equals("nil"))
            element = null;
        handler.handle(UnitResponse.createSuccess(element));
    }

}
