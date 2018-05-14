package info.xiancloud.cache.service.unit.set;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.redis.util.FormatUtil;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;
import redis.clients.jedis.Jedis;

import java.util.Iterator;
import java.util.Set;

/**
 * Set SREM
 *
 * @author John_zero, happyyangyuan
 */
public class CacheSetRemovesUnit implements Unit {
    @Override
    public String getName() {
        return "cacheSetRemoves";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("Set SREM").setDocApi(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", String.class, "", REQUIRED)
                .add("member", String.class, "", NOT_REQUIRED)
                .add("members", Set.class, "", NOT_REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        String key = msg.get("key", String.class);
        Object member = msg.get("member", Object.class);
        Set<Object> members = msg.get("members");
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        long result = 0L;
        try (Jedis jedis = Redis.useDataSource(cacheConfigBean).getResource()) {
            if (members != null && !members.isEmpty()) {
                String[] _members = new String[members.size()];

                Iterator<Object> iterator = members.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    _members[i] = FormatUtil.formatValue(iterator.next());
                    i++;
                }

                result = jedis.srem(key, _members);
            } else if (member != null && !"".equals(member)) {
                result = jedis.srem(key, FormatUtil.formatValue(member));
            }
        }
        handler.handle(UnitResponse.createSuccess(result));
    }

}
