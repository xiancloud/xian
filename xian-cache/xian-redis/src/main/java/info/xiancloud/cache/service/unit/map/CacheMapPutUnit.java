package info.xiancloud.cache.service.unit.map;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.redis.util.FormatUtil;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;

/**
 * Map Put
 *
 * @author John_zero, happyyangyuan
 */
public class CacheMapPutUnit implements Unit {
    @Override
    public String getName() {
        return "cacheMapPut";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("Map Put").setDocApi(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", String.class, "缓存的关键字", REQUIRED)
                .add("field", String.class, "Value KEY", REQUIRED)
                .add("value", Object.class, "缓存的内容", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) throws Exception {
        String key = msg.getArgMap().get("key").toString();
        String field = msg.getArgMap().get("field").toString();
        Object valueObj = msg.getArgMap().get("value");
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        long result = Redis.call(cacheConfigBean, jedis -> {
            String value = FormatUtil.formatValue(valueObj);
            return jedis.hset(key, field, value);
        });

        if (result == 0) {
            handler.handle(UnitResponse.createSuccess(null, "存在, 覆盖"));
            return;
        } else if (result == 1) {
            handler.handle(UnitResponse.createSuccess(null, "新建, 设置"));
            return;
        }
        handler.handle(UnitResponse.createSuccess());
    }

}
