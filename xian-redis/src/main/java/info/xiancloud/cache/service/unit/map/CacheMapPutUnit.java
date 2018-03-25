package info.xiancloud.cache.service.unit.map;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.redis.util.FormatUtil;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;

/**
 * Map Put
 *
 * @author John_zero
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
        return UnitMeta.create().setDescription("Map Put").setPublic(false);
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
    public UnitResponse execute(UnitRequest msg) {
        String key = msg.getArgMap().get("key").toString();
        String field = msg.getArgMap().get("field").toString();
        Object valueObj = msg.getArgMap().get("value");
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        try {
            long result = Redis.call(cacheConfigBean, jedis -> {
                String value = FormatUtil.formatValue(valueObj);
                return jedis.hset(key, field, value);
            });

            if (result == 0)
                return UnitResponse.success("存在, 覆盖");
            else if (result == 1)
                return UnitResponse.success("新建, 设置");
        } catch (Exception e) {
            return UnitResponse.exception(e);
        }
        return UnitResponse.success();
    }

}
