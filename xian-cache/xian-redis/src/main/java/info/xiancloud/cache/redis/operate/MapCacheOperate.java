package info.xiancloud.cache.redis.operate;

import redis.clients.jedis.Jedis;

/**
 * Map 结构
 *
 * @author John_zero, happyyangyuan
 * http://redisdoc.com/hash/index.html
 */
public final class MapCacheOperate {

    public static boolean exists(Jedis jedis, String key, String field) {
        return jedis.hexists(key, field);
    }

    /**
     * 删除 Map 中的属性
     *
     * @param jedis
     * @param key
     * @param fields
     * @return
     */
    public static long remove(Jedis jedis, String key, String[] fields) {
        return jedis.hdel(key, fields);
    }

    /**
     * 删除整个 Map
     *
     * @param jedis
     * @param key
     * @return
     */
    public static long removeAll(Jedis jedis, String key) {
        return jedis.del(key);
    }

}
