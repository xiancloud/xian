package info.xiancloud.redis;

import info.xiancloud.plugin.conf.EnvConfig;
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
            protected void toContinue(UnitResponse unitResponse) {
                System.out.println(unitResponse);
            }
        });
    }

    public static String getHost() {
        return EnvConfig.get("redis.host");
    }

    public static String getPassword() {
        return EnvConfig.get("redisPassword");
    }

    public static int getPort() {
        return EnvConfig.getIntValue("redis.port", 6379);
    }

    public static int getDBIndex() {
        return EnvConfig.getIntValue("redisDbIndex", 0);
    }
}