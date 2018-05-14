package info.xiancloud.redis;

import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.message.SingleRxXian;

import java.util.HashMap;

/**
 * @author John_zero, happyyangyuan
 */
public class RedisTest extends BaseRedis {

    public static void main(String[] args) {
        SingleRxXian.call("cache", "jedisInit", new HashMap<String, Object>() {{
            put("host", getHost());
            put("password", getPassword());
            put("port", getPort());
            put("dbIndex", getDBIndex());
        }}).blockingGet();
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