package info.xiancloud.cache.service.unit.system;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;

/**
 * add datasource into JedisPool
 *
 * @author John_zero, happyyangyuan
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
        return UnitMeta.createWithDescription("JedisPool 新增").setDocApi(false);
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
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        String host = msg.get("host", String.class);
        int port = msg.getArgMap().containsKey("port") ? msg.get("port", int.class) : Redis.PORT;
        String password = msg.getArgMap().containsKey("password") ? msg.get("password", String.class) : "";
        int dbIndex = msg.getArgMap().containsKey("dbIndex") ? msg.get("dbIndex", int.class) : Redis.DB_INDEX;

        Redis.initRedis(host, port, password, dbIndex);
        handler.handle(UnitResponse.createSuccess());
    }

}
