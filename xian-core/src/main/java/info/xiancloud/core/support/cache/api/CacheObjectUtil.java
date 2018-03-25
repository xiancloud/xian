package info.xiancloud.core.support.cache.api;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.message.SyncXian;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;
import info.xiancloud.core.support.cache.CacheService;
import info.xiancloud.core.support.cache.vo.ScanVo;
import info.xiancloud.core.util.Reflection;

import java.util.List;
import java.util.Set;

/**
 * cache Object Util
 *
 * @author John_zero
 */
public final class CacheObjectUtil {

    private CacheObjectUtil() {

    }

    @Deprecated
    public static Object luaScript(String scripts, int keyCount, List<String> params) {
        return luaScript(CacheService.CACHE_CONFIG_BEAN, scripts, keyCount, params);
    }

    @Deprecated
    public static Object luaScript(CacheConfigBean cacheConfigBean, String scripts, int keyCount, List<String> params) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheLua", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("scripts", scripts);
            put("keyCount", keyCount);
            put("params", params);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();

        if (unitResponseObject.getData() == null)
            return null;

        return unitResponseObject.getData();
    }

    @Deprecated
    public static Object luaScript(String scripts, List<String> keys, List<String> params) {
        return luaScript(CacheService.CACHE_CONFIG_BEAN, scripts, keys, params);
    }

    @Deprecated
    public static Object luaScript(CacheConfigBean cacheConfigBean, String scripts, List<String> keys, List<String> params) {
        UnitResponse response = SyncXian.call(CacheService.CACHE_SERVICE, "cacheLua", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("scripts", scripts);
            put("keys", keys);
            put("params", params);
        }});
        response.throwExceptionIfNotSuccess();
        return response.getData();
    }

    public static Set<String> keys(String pattern) {
        return keys(CacheService.CACHE_CONFIG_BEAN, pattern);
    }

    public static Set<String> keys(CacheConfigBean cacheConfigBean, String pattern) {
        UnitResponse response = SyncXian.call(CacheService.CACHE_SERVICE, "cacheKeys", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("pattern", pattern);
        }});
        response.throwExceptionIfNotSuccess();
        return response.getData();
    }

    public static boolean exists(String cacheKey) {
        return exists(CacheService.CACHE_CONFIG_BEAN, cacheKey);
    }

    public static boolean exists(CacheConfigBean cacheConfigBean, String cacheKey) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheExists", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();

        return (boolean) unitResponseObject.getData();
    }

    public static boolean set(String cacheKey, Object value) {
        return set(CacheService.CACHE_CONFIG_BEAN, cacheKey, value);
    }

    public static boolean set(CacheConfigBean cacheConfigBean, String cacheKey, Object value) {
        UnitResponse response = SyncXian.call(CacheService.CACHE_SERVICE, "cacheObject", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("value", value);
        }});
        response.throwExceptionIfNotSuccess();
        return response.succeeded();
    }

    public static boolean set(String cacheKey, Object value, int timeoutInSecond) {
        return set(CacheService.CACHE_CONFIG_BEAN, cacheKey, value, timeoutInSecond);
    }

    public static boolean set(CacheConfigBean cacheConfigBean, String cacheKey, Object value, int timeoutInSecond) {
        UnitResponse response = SyncXian.call(CacheService.CACHE_SERVICE, "cacheObject", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("value", value);
            put("timeout", timeoutInSecond);
        }});
        response.throwExceptionIfNotSuccess();
        return response.succeeded();
    }

    public static <T> T get(String cacheKey, Class<T> clazz) {
        return get(CacheService.CACHE_CONFIG_BEAN, cacheKey, clazz);
    }

    public static <T> T get(CacheConfigBean cacheConfigBean, String cacheKey, Class<T> clazz) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheObjectGet", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();

        return Reflection.toType(unitResponseObject.getData(), clazz);
    }

    public static boolean remove(String cacheKey) {
        return remove(CacheService.CACHE_CONFIG_BEAN, cacheKey);
    }

    public static boolean remove(CacheConfigBean cacheConfigBean, String cacheKey) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheObjectRemove", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();
        return unitResponseObject.succeeded();
    }

    public static long increment(String cacheKey) {
        return increment(CacheService.CACHE_CONFIG_BEAN, cacheKey);
    }

    public static long increment(CacheConfigBean cacheConfigBean, String cacheKey) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheIncrement", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();

        return (long) unitResponseObject.getData();
    }

    public static long incrementByValue(String cacheKey, long value) {
        return incrementByValue(CacheService.CACHE_CONFIG_BEAN, cacheKey, value);
    }

    public static long incrementByValue(CacheConfigBean cacheConfigBean, String cacheKey, long value) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheIncrement", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("value", value);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();
        return (long) unitResponseObject.getData();
    }

    public static long incrementByValue(String cacheKey, long value, int timeoutInSecond) {
        return incrementByValue(CacheService.CACHE_CONFIG_BEAN, cacheKey, value, timeoutInSecond);
    }

    public static long incrementByValue(CacheConfigBean cacheConfigBean, String cacheKey, long value, int timeoutInSecond) {
        UnitResponse response = SyncXian.call(CacheService.CACHE_SERVICE, "cacheIncrement", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("value", value);
            put("timeout", timeoutInSecond);
        }});
        response.throwExceptionIfNotSuccess();
        return (long) response.getData();
    }

    public static long decrement(String cacheKey) {
        return decrement(CacheService.CACHE_CONFIG_BEAN, cacheKey);
    }

    public static long decrement(CacheConfigBean cacheConfigBean, String cacheKey) {
        UnitResponse response = SyncXian.call(CacheService.CACHE_SERVICE, "cacheDecrement", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }});
        response.throwExceptionIfNotSuccess();
        return (long) response.getData();
    }

    public static long decrementByValue(String cacheKey, long value) {
        return decrementByValue(CacheService.CACHE_CONFIG_BEAN, cacheKey, value);
    }

    public static long decrementByValue(CacheConfigBean cacheConfigBean, String cacheKey, long value) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheDecrement", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("value", value);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();
        return (long) unitResponseObject.getData();
    }

    public static long decrementByValue(String cacheKey, long value, int timeoutInSecond) {
        return decrementByValue(CacheService.CACHE_CONFIG_BEAN, cacheKey, value, timeoutInSecond);
    }

    public static long decrementByValue(CacheConfigBean cacheConfigBean, String cacheKey, long value, int timeoutInSecond) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheDecrement", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("value", value);
            put("timeout", timeoutInSecond);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();

        return (long) unitResponseObject.getData();
    }

    public static long ttl(CacheConfigBean cacheConfigBean, String cacheKey) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheTtl", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();

        return (long) unitResponseObject.getData();
    }

    public static String type(CacheConfigBean cacheConfigBean, String cacheKey) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheType", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();
        return (String) unitResponseObject.getData();
    }

    /**
     * @param pattern pattern
     * @param count   count
     * @param cursor  "0" 开始新一轮迭代
     * @return cursor "0" 结束完成迭代
     * @deprecated 不建议使用该 API, 各种不稳定
     */
    public static ScanVo scan(String pattern, int count, String cursor) {
        return scan(CacheService.CACHE_CONFIG_BEAN, pattern, count, cursor);
    }

    /**
     * @param cacheConfigBean cacheConfigBean
     * @param pattern         pattern
     * @param count           count
     * @param cursor          cursor
     * @return ScanVo
     * @deprecated 不建议使用该 API, 各种不稳定
     */
    public static ScanVo scan(CacheConfigBean cacheConfigBean, String pattern, int count, String cursor) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheScan", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("pattern", pattern);
            put("count", count);
            put("cursor", cursor);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();
        JSONObject jsonObject = unitResponseObject.getData();
        return new ScanVo(jsonObject);
    }

}
