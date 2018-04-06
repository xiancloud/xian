package info.xiancloud.cache.service.unit.list;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;

/**
 * List Clear
 *
 * @author John_zero, happyyangyuan
 */
public class CacheListClearUnit implements Unit {
    @Override
    public String getName() {
        return "cacheListClear";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("List Clear").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", Object.class, "缓存的关键字", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        String key = msg.get("key");
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        try {
            Redis.call(cacheConfigBean, (jedis) -> jedis.ltrim(key, 1, 0));
            handler.handle(UnitResponse.createSuccess());
        } catch (Exception e) {
            handler.handle(UnitResponse.createException(e));
        }
    }

}
