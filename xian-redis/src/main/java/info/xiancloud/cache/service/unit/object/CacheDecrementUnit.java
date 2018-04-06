package info.xiancloud.cache.service.unit.object;

import info.xiancloud.cache.CacheOperateManager;
import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.redis.operate.ObjectCacheOperate;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;
import redis.clients.jedis.Jedis;

/**
 * numeric cache decrement
 *
 * @author John_zero
 */
public class CacheDecrementUnit implements Unit {
    @Override
    public String getName() {
        return "cacheDecrement";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("自减").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", Object.class, "缓存的关键字", REQUIRED)
                .add("value", Long.class, "自减的值", NOT_REQUIRED)
                .add("timeout", Integer.class, "超时时间, 单位: 秒", NOT_REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        String key = msg.getArgMap().get("key").toString();
        Long value = msg.get("value", Long.class);
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        long decrement = 0;
        try (Jedis jedis = Redis.useDataSource(cacheConfigBean).getResource()) {
            if (value != null)
                decrement = ObjectCacheOperate.decrBy(jedis, key, value);
            else
                decrement = ObjectCacheOperate.decr(jedis, key);

            if (msg.getArgMap().containsKey("timeout")) {
                int timeout = CacheOperateManager.correctionTimeout(msg.get("timeout", int.class));
                if (timeout > -1)
                    ObjectCacheOperate.expire(jedis, key, timeout);
            }
        } catch (Exception e) {
            handler.handle(UnitResponse.createException(e));
            return;
        }
        handler.handle(UnitResponse.createSuccess(decrement));
    }

}
