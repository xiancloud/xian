package info.xiancloud.cache.service.unit.list;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.redis.util.FormatUtil;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;

/**
 * List Set
 *
 * @author John_zero
 */
public class CacheListSetUnit implements Unit {
    @Override
    public String getName() {
        return "cacheListSet";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("List Set").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", Object.class, "缓存的关键字", REQUIRED)
                .add("index", int.class, "", REQUIRED)
                .add("valueObj", Object.class, "", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        String key = msg.get("key", String.class);
        int index = msg.get("index", int.class, 0);
        Object valueObj = msg.get("valueObj");
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        try {
            String replyCode = Redis.call(cacheConfigBean, (jedis) -> {
                String value = FormatUtil.formatValue(valueObj);
                return jedis.lset(key, index, value);
            });

            if ("OK".equals(replyCode)) {
                handler.handle(UnitResponse.createSuccess());
                return;
            } else {
                handler.handle(UnitResponse.createUnknownError(replyCode, replyCode));
                return;
            }
        } catch (Exception e) {
            handler.handle(UnitResponse.createException(e));
            return;
        }
    }

}
