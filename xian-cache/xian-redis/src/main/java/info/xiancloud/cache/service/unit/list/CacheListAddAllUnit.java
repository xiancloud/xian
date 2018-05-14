package info.xiancloud.cache.service.unit.list;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.redis.operate.ServerOperate;
import info.xiancloud.cache.redis.util.FormatUtil;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.List;

/**
 * List AddAll
 *
 * @author John_zero, happyyyangyaun
 */
public class CacheListAddAllUnit implements Unit {
    @Override
    public String getName() {
        return "cacheListAddAll";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("List AddAll").setDocApi(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", String.class, "缓存的关键字", REQUIRED)
                .add("values", List.class, "", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        String key = msg.get("key", String.class);
        List values = msg.get("values", List.class);
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        try (Jedis jedis = Redis.useDataSource(cacheConfigBean).getResource()) {
            if (values != null && !values.isEmpty()) {
                String info = ServerOperate.info(jedis, "server");
                String redis_mode = ServerOperate.getAttributeInInfo(info, "redis_mode");
                if (redis_mode != null && "standalone".equals(redis_mode)) {
                    Pipeline pipeline = jedis.pipelined();
                    values.forEach(valueObj -> {
                        String value = FormatUtil.formatValue(valueObj);
                        pipeline.rpush(key, value);
                    });
                    pipeline.sync();
                } else {
                    values.forEach(valueObj -> {
                        String value = FormatUtil.formatValue(valueObj);
                        jedis.rpush(key, value);
                    });
                }
            }
        } catch (Exception e) {
            handler.handle(UnitResponse.createException(e));
            return;
        }
        handler.handle(UnitResponse.createSuccess());
    }

}
