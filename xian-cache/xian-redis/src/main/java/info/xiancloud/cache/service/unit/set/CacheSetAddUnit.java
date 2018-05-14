package info.xiancloud.cache.service.unit.set;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.redis.util.FormatUtil;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;

import java.util.Iterator;
import java.util.Set;

/**
 * Set Add
 * <p>
 * http://doc.redisfans.com/set/sadd.html
 *
 * @author John_zero, happyyangyuan
 */
public class CacheSetAddUnit implements Unit {
    @Override
    public String getName() {
        return "cacheSetAdd";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("Set Add").setDocApi(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", String.class, "", REQUIRED)
                .add("members", Set.class, "", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) throws Exception {
        String key = msg.get("key", String.class);
        Set members = msg.get("members", Set.class);
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        Long result = 0L;

        if (members != null && !members.isEmpty()) {
            result = Redis.call(cacheConfigBean, jedis -> {
                String[] _members = new String[members.size()];

                Iterator iterator = members.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    _members[i] = FormatUtil.formatValue(iterator.next());
                    i++;
                }
                return jedis.sadd(key, _members);
            });
        }
        handler.handle(UnitResponse.createSuccess(result));
    }

}
