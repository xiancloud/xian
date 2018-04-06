package info.xiancloud.cache.service.unit.object;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.redis.operate.ObjectCacheOperate;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;
import redis.clients.jedis.Jedis;

/**
 * cache set
 * http://doc.redisfans.com/string/set.html
 *
 * @author John_zero
 */
public class CacheSetUnit implements Unit {
    @Override
    public String getName() {
        return "cacheSet";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("缓存 存储").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", Object.class, "缓存的关键字", REQUIRED)
                .add("value", Object.class, "缓存的内容", REQUIRED)
                .add("ex", int.class, "EX second: 设置键的过期时间为 second 秒. SET key value EX second 效果等同于 SETEX key second value, 单位: 秒", NOT_REQUIRED)
                .add("px", long.class, "PX millisecond: 设置键的过期时间为 millisecond 毫秒. SET key value PX millisecond 效果等同于 PSETEX key millisecond value, 单位: 毫秒", NOT_REQUIRED)
                .add("nxXx", String.class, "NX: 只在键不存在时, 才对键进行设置操作, SET key value NX 效果等同于 SETNX key value; XX: 只在键已经存在时, 才对键进行设置操作", NOT_REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        String key = msg.getArgMap().get("key").toString();
        Object value = msg.getArgMap().get("value");
        int ex = msg.get("ex", int.class, -1);
        long px = msg.get("px", int.class, -2);
        String nxXx = msg.get("nxXx", String.class, null);
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        String result;
        try (Jedis jedis = Redis.useDataSource(cacheConfigBean).getResource()) {
            if (px > -2)
                result = ObjectCacheOperate.set(jedis, key, value, "PX", px, nxXx);
            else
                result = ObjectCacheOperate.set(jedis, key, value, "EX", ex, nxXx);
        } catch (Exception e) {
            handler.handle(UnitResponse.createException(e));
            return;
        }

        if ("OK".equals(result)) {
            handler.handle(UnitResponse.createSuccess());
        } else {
            handler.handle(UnitResponse.createUnknownError(result, result));
        }
    }

}
