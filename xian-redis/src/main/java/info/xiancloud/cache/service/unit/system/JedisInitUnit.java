package info.xiancloud.cache.service.unit.system;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;

/**
 * Jedis 初始化
 *
 * @author John_zero
 * @deprecated need further test
 */
public class JedisInitUnit implements Unit {
    @Override
    public String getName() {
        return "jedisInit";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("Jedis 初始化").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("url", String.class, "", REQUIRED)
                .add("password", String.class, "", NOT_REQUIRED)
                .add("dbIndex", int.class, "", NOT_REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String url = msg.get("url", String.class);
        String password = msg.getArgMap().containsKey("password") ? msg.get("password", String.class) : "";
        int dbIndex = msg.getArgMap().containsKey("dbIndex") ? msg.get("dbIndex", int.class) : Redis.DB_INDEX;
        Redis.initRedis(url, password, dbIndex);
        return UnitResponse.createSuccess();
    }

}
