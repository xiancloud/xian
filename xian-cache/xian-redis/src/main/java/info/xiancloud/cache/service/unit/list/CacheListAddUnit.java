package info.xiancloud.cache.service.unit.list;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.redis.util.FormatUtil;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;

/**
 * List add operation.
 *
 * @author John_zero, happyyangyuan
 */
public class CacheListAddUnit implements Unit {
    @Override
    public String getName() {
        return "cacheListAdd";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("List Add").setDocApi(false)
                ;
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", Object.class, "缓存的key", REQUIRED)
                .add("valueObj", Object.class, "", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "缓存数据源", NOT_REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        String key = msg.getArgMap().get("key").toString();
        Object valueObj = msg.getArgMap().get("valueObj");
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        try {
            long length = Redis.call(cacheConfigBean, (jedis) -> {
                String value = FormatUtil.formatValue(valueObj);
                return jedis.rpush(key, value);
            });
            handler.handle(UnitResponse.createSuccess(length));
        } catch (Exception e) {
            handler.handle(UnitResponse.createException(e));
        }
    }

}
