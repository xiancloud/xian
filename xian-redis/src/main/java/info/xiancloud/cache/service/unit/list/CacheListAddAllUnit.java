package info.xiancloud.cache.service.unit.list;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.redis.operate.ServerOperate;
import info.xiancloud.cache.redis.util.FormatUtil;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.support.cache.CacheConfigBean;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.List;

/**
 * List AddAll
 *
 * @author John_zero
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
        return UnitMeta.create().setDescription("List AddAll").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", String.class, "缓存的关键字", REQUIRED)
                .add("values", List.class, "", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String key = msg.get("key", String.class);
        List values = msg.get("values", List.class);
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        try (Jedis jedis = Redis.useDataSource(cacheConfigBean).getResource()) {
            if (values != null && !values.isEmpty()) {
                String info = ServerOperate.info(jedis, "server");
                String redis_mode = ServerOperate.getAttributeInInfo(info, "redis_mode");
                if (redis_mode != null && "standalone".equals(redis_mode)) {
                    Pipeline pipeline = jedis.pipelined();
                    values.stream().forEach(valueObj -> {
                        String value = FormatUtil.formatValue(valueObj);
                        pipeline.rpush(key, value);
                    });
                    pipeline.sync();
                } else {
                    values.stream().forEach(valueObj -> {
                        String value = FormatUtil.formatValue(valueObj);
                        jedis.rpush(key, value);
                    });
                }
            }
        } catch (Exception e) {
            return UnitResponse.exception(e);
        }
        return UnitResponse.success();
    }

}
