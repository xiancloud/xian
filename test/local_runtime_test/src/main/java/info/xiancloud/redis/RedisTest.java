package info.xiancloud.redis;

import info.xiancloud.plugin.conf.XianConfig;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.Xian;
import info.xiancloud.plugin.support.mq.mqtt.handle.NotifyHandler;

import java.util.HashMap;

/**
 * @author John_zero
 */
public class RedisTest extends BaseRedis {

    public static void main(String[] args) {
        Xian.call("cache", "jedisInit", new HashMap<String, Object>() {{
            put("host", getHost());
            put("password", getPassword());
            put("port", getPort());
            put("dbIndex", getDBIndex());
        }}, new NotifyHandler() {
            @Override
            protected void handle(UnitResponse unitResponse) {
                System.out.println(unitResponse);
            }
        });
    }

    public static String getHost() {
        return XianConfig.get("redis.host");
    }

    public static String getPassword() {
        return XianConfig.get("redisPassword");
    }

    public static int getPort() {
        return XianConfig.getIntValue("redis.port", 6379);
    }

    public static int getDBIndex() {
        return XianConfig.getIntValue("redisDbIndex", 0);
    }
}