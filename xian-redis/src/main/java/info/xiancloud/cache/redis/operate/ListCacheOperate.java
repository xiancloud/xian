package info.xiancloud.cache.redis.operate;

import info.xiancloud.cache.redis.util.FormatUtil;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * List 结构
 * http://redisdoc.com/list/index.html
 *
 * @author John_zero
 */
public final class ListCacheOperate {

    public static List<String> range(Jedis jedis, String key, long startIndex, long endIndex) {
        return jedis.lrange(key, startIndex, endIndex);
    }

    /**
     * List 移除元素
     *
     * @param jedis    jedis object
     * @param key      the cache key
     * @param valueObj the value object
     * @return 注意:
     * if count is greater than 0 : 从表头开始向表尾搜索, 移除与 VALUE 相等的元素, 数量为 COUNT
     * if count is lower than 0 : 从表尾开始向表头搜索, 移除与 VALUE 相等的元素, 数量为 COUNT 的绝对值
     * if count equals 0 : 移除列表中所有与 VALUE 相等的值
     */
    public static long remove(Jedis jedis, String key, Object valueObj) {
        final int count = 1; // > 0

        String value = FormatUtil.formatValue(valueObj);

        return jedis.lrem(key, count, value);
    }

}
