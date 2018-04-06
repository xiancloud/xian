package info.xiancloud.cache.service.unit.set;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.redis.util.FormatUtil;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;
import io.reactivex.Single;

/**
 * Set SISMEMBER
 *
 * @author John_zero
 */
public class CacheSetSisMemberUnit implements Unit {
    @Override
    public String getName() {
        return "cacheSetSisMember";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("Set SISMEMBER").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", String.class, "", REQUIRED)
                .add("member", String.class, "", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        String key = msg.get("key", String.class);
        Object member = msg.get("member", Object.class);
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        try {
            Boolean result = Redis.call(cacheConfigBean, jedis -> jedis.sismember(key, FormatUtil.formatValue(member)));
            handler.handle(UnitResponse.createSuccess(result));
        } catch (Throwable e) {
            handler.handle(UnitResponse.createException(e));
        }
    }

}
