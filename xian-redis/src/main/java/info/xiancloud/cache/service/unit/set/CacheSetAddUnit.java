package info.xiancloud.cache.service.unit.set;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.redis.util.FormatUtil;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
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
 * @author John_zero
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
        return UnitMeta.create("Set Add").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", String.class, "", REQUIRED)
                .add("members", Set.class, "", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String key = msg.get("key", String.class);
        Set members = (Set) msg.getArgMap().get("members");
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        try {
            Long result = 0L;

            if (members != null && !members.isEmpty()) {
                result = Redis.call(cacheConfigBean, jedis -> {
                    String[] _members = new String[members.size()];

                    Iterator<Object> iterator = members.iterator();
                    int i = 0;
                    while (iterator.hasNext()) {
                        _members[i] = FormatUtil.formatValue(iterator.next());
                        i++;
                    }
                    return jedis.sadd(key, _members);
                });
            }
            return UnitResponse.success(result);
        } catch (Throwable e) {
            return UnitResponse.exception(e);
        }
    }

}
