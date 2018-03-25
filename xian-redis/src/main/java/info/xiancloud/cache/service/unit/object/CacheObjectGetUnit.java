package info.xiancloud.cache.service.unit.object;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;

/**
 * cache getter
 *
 * @author John_zero
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
        return UnitMeta.create("缓存获取").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input().add("key", Object.class, "缓存的关键字", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String key = msg.getArgMap().get("key").toString();
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        try {
            Object result = Redis.call(cacheConfigBean, jedis -> jedis.get(key));

            if (result != null && result.toString().equals("nil"))
                result = null;
            return UnitResponse.success(result);
        } catch (Exception e) {
            return UnitResponse.exception(e);
        }
    }

}
