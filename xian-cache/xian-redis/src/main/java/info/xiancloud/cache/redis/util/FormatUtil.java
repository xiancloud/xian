package info.xiancloud.cache.redis.util;

import info.xiancloud.core.util.Reflection;

/**
 * 格式化
 *
 * @author John_zero, happyyangyuan
 */
public final class FormatUtil {

    public static String formatValue(Object valueObj) {
        return Reflection.toType(valueObj, String.class);
    }

}
