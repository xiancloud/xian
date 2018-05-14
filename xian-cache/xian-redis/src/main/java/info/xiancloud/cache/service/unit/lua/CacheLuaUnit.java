package info.xiancloud.cache.service.unit.lua;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * Lua Script
 * <p>
 * http://doc.redisfans.com/script/eval.html#script
 * http://redisbook.readthedocs.io/en/latest/feature/scripting.html
 * https://redis.io/commands/eval
 *
 * @author John_zero, happyyangyuan
 */
public class CacheLuaUnit implements Unit {
    @Override
    public String getName() {
        return "cacheLua";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("Lua Script").setDocApi(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("scripts", String.class, "", REQUIRED)
                .add("keyCount", List.class, "", NOT_REQUIRED)
                .add("keys", List.class, "", NOT_REQUIRED)
                .add("params", List.class, "", NOT_REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        String scripts = msg.get("scripts", String.class);
        Integer keyCount = msg.get("keyCount", Integer.class);
        List<String> keys = msg.get("keys");
        List<String> params = msg.get("params");
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        Object response;
        try (Jedis jedis = Redis.useDataSource(cacheConfigBean).getResource()) {
            if (keyCount != null && params != null) {
                response = jedis.eval(scripts, keyCount, params.toArray(new String[params.size()]));
            } else if (keys != null && params != null) {
                response = jedis.eval(scripts, keys, params);
            } else {
                response = jedis.eval(scripts);
            }
        } catch (Exception e) {
            handler.handle(UnitResponse.createException(e));
            return;
        }
        handler.handle(UnitResponse.createSuccess(response));
    }

}
