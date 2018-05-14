package info.xiancloud.cache.service.unit.sorted_set;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.redis.util.FormatUtil;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;

/**
 * Sorted Set Add
 * <p>
 * http://doc.redisfans.com/sorted_set/zadd.html
 *
 * @author John_zero, happyyangyuan
 */
public class CacheSortedSetAddUnit implements Unit {
    @Override
    public String getName() {
        return "cacheSortedSetAdd";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("Sorted Set Add").setDocApi(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", String.class, "", REQUIRED)
                .add("score", Long.class, "", REQUIRED)
                .add("member", Object.class, "", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) throws Exception {
        String key = msg.get("key", String.class);
        Long score = (Long) msg.getArgMap().get("score");
        Object member = msg.getArgMap().get("member");
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        Long result = Redis.call(cacheConfigBean, jedis -> jedis.zadd(key, score, FormatUtil.formatValue(member)));
        handler.handle(UnitResponse.createSuccess(result));
    }

}
