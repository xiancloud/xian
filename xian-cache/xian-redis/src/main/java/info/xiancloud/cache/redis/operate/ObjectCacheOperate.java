package info.xiancloud.cache.redis.operate;

import info.xiancloud.cache.redis.util.FormatUtil;
import redis.clients.jedis.Jedis;

/**
 * @author John_zero, happyyangyuan
 */
public final class ObjectCacheOperate {

    public static long del(Jedis jedis, String key) {
        return jedis.del(key);
    }

    public static boolean exists(Jedis jedis, String key) {
        return jedis.exists(key);
    }

    public static long expire(Jedis jedis, String key, int seconds) {
        return jedis.expire(key, seconds);
    }

    public static String get(Jedis jedis, String key) {
        return jedis.get(key);
    }

    public static void set(Jedis jedis, String key, Object valueObj) {
        String value = FormatUtil.formatValue(valueObj);

        jedis.set(key, value);
    }

    public static String set(Jedis jedis, String key, Object valueObj, String exPx, long time, String nxXx) {
        String value = FormatUtil.formatValue(valueObj);

        return jedis.set(key, value, nxXx, exPx, time);
    }

    public static void setex(Jedis jedis, String key, Object valueObj, int seconds) {
        String value = FormatUtil.formatValue(valueObj);

        jedis.setex(key, seconds, value);
    }

    public static long incr(Jedis jedis, String key) {
        return jedis.incr(key);
    }

    /**
     * @param jedis the jedis connection.
     * @param key   the key
     * @param increment the increment
     * @return according to jedis javadoc, the new value after increment is returned.
     */
    public static long incrBy(Jedis jedis, String key, long increment) {
        return jedis.incrBy(key, increment);
    }

    public static long decr(Jedis jedis, String key) {
        return jedis.decr(key);
    }

    public static long decrBy(Jedis jedis, String key, long value) {
        return jedis.decrBy(key, value);
    }

}
