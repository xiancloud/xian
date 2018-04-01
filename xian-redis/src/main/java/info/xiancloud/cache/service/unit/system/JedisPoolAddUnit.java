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
 * 新增 JedisPool
 *
 * @author John_zero
 */
public class JedisPoolAddUnit implements Unit {
    @Override
    public String getName() {
        return "jedisPoolAdd";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("JedisPool 新增").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("host", String.class, "", REQUIRED)
                .add("port", int.class, "", NOT_REQUIRED)
                .add("password", String.class, "", NOT_REQUIRED)
                .add("dbIndex", int.class, "", NOT_REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String host = msg.get("host", String.class);
        int port = msg.getArgMap().containsKey("port") ? msg.get("port", int.class) : Redis.PORT;
        String password = msg.getArgMap().containsKey("password") ? msg.get("password", String.class) : "";
        int dbIndex = msg.getArgMap().containsKey("dbIndex") ? msg.get("dbIndex", int.class) : Redis.DB_INDEX;

        Redis.initRedis(host, port, password, dbIndex);
        return UnitResponse.createSuccess();
    }

}
