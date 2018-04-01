package info.xiancloud.cache.service.unit.map;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.redis.operate.MapCacheOperate;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;

/**
 * Map Batch Remove
 *
 * @author John_zero
 */
public class CacheMapBatchRemovesUnit implements Unit {
    @Override
    public String getName() {
        return "cacheMapBatchRemove";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("Map Batch Remove").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("batchRemoves", Map.class, "批量移除列表", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        Map<String, List<String>> batchRemoves = (Map<String, List<String>>) msg.getArgMap().get("batchRemoves");
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        if (batchRemoves != null && !batchRemoves.isEmpty()) {
            try (Jedis jedis = Redis.useDataSource(cacheConfigBean).getResource()) {
                for (Map.Entry<String, List<String>> entry : batchRemoves.entrySet()) {
                    if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                        String[] fields = entry.getValue().toArray(new String[]{});
                        MapCacheOperate.remove(jedis, entry.getKey(), fields);
                    }
                }
            } catch (Exception e) {
                return UnitResponse.createException(e);
            }
        }
        return UnitResponse.createSuccess();
    }

}
