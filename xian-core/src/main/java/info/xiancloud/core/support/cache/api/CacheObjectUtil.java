package info.xiancloud.core.support.cache.api;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.support.cache.CacheConfigBean;
import info.xiancloud.core.support.cache.CacheService;
import info.xiancloud.core.support.cache.vo.ScanVo;
import info.xiancloud.core.util.Reflection;
import io.reactivex.Completable;
import io.reactivex.Single;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * cache Object Util
 *
 * @author John_zero, happyyangyuan for asynchronous refactor
 */
public final class CacheObjectUtil {

    private CacheObjectUtil() {
    }

    /**
     * execute the lua script
     *
     * @param scripts  the script to be executed.
     * @param keyCount the key count
     * @param params   the parameters
     * @return the result object
     */
    public static Object luaScript(String scripts, int keyCount, List<String> params) {
        return luaScript(CacheService.CACHE_CONFIG_BEAN, scripts, keyCount, params);
    }

    /**
     * execute the specified script.
     *
     * @param cacheConfigBean the data source of the cache
     * @param scripts         the lua script.
     * @param keyCount        the key count.
     * @param params          the parameters.
     * @return the object result.
     */
    public static Single<Object> luaScript(CacheConfigBean cacheConfigBean, String scripts, int keyCount, List<String> params) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheLua", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("scripts", scripts);
            put("keyCount", keyCount);
            put("params", params);
        }}).map(unitResponseObject -> {
            unitResponseObject.throwExceptionIfNotSuccess();
            if (unitResponseObject.getData() == null)
                return null;
            return unitResponseObject.getData();
        });
    }

    /**
     * @param scripts the lua script
     * @param keys    the keys
     * @param params  the parameters.
     * @return the result object.
     */
    public static Single<Object> luaScript(String scripts, List<String> keys, List<String> params) {
        return luaScript(CacheService.CACHE_CONFIG_BEAN, scripts, keys, params);
    }

    /**
     * @param cacheConfigBean the data source of the cache
     * @param scripts         the lua script
     * @param keys            the keys
     * @param params          the parameters
     * @return the result object.
     */
    public static Single<Object> luaScript(CacheConfigBean cacheConfigBean, String scripts, List<String> keys, List<String> params) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheLua", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("scripts", scripts);
            put("keys", keys);
            put("params", params);
        }}).map(response -> {
            response.throwExceptionIfNotSuccess();
            return response.getData();
        });

    }

    /**
     * @param pattern the pattern
     * @return the keys
     */
    public static Single<Set<String>> keys(String pattern) {
        return keys(CacheService.CACHE_CONFIG_BEAN, pattern);
    }

    /**
     * get the keys who's value matches the pattern
     *
     * @param cacheConfigBean the data source of the cache
     * @param pattern         the pattern
     * @return the keys
     */
    @SuppressWarnings("unchecked")
    public static Single<Set<String>> keys(CacheConfigBean cacheConfigBean, String pattern) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheKeys", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("pattern", pattern);
        }}).map(response -> {
            response.throwExceptionIfNotSuccess();
            if (response.getData() instanceof Set) {
                return (Set<String>) response.getData();
            } else
                return new HashSet<String>() {{
                    addAll(response.dataToTypedList(String.class));
                }};
        });
    }

    /**
     * check whether the key exists.
     *
     * @param cacheKey the key of the cache
     * @return true if exists, false otherwise.
     */
    public static Single<Boolean> exists(String cacheKey) {
        return exists(CacheService.CACHE_CONFIG_BEAN, cacheKey);
    }

    /**
     * check whether the key exists.
     *
     * @param cacheConfigBean the cache data source.
     * @param cacheKey        the key of the key
     * @return true if exists otherwise false.
     */
    public static Single<Boolean> exists(CacheConfigBean cacheConfigBean, String cacheKey) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheExists", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }}).map(unitResponse -> {
            unitResponse.throwExceptionIfNotSuccess();
            return (boolean) unitResponse.getData();
        });
    }

    /**
     * set the value of the cache object.
     *
     * @param cacheKey the key
     * @param value    the value
     * @return complete or error.
     */
    public static Completable set(String cacheKey, Object value) {
        return set(CacheService.CACHE_CONFIG_BEAN, cacheKey, value);
    }

    /**
     * set the value of the cache object.
     *
     * @param cacheConfigBean the data source fo the cache
     * @param cacheKey        the key
     * @param value           the value
     * @return complete or error.
     */
    public static Completable set(CacheConfigBean cacheConfigBean, String cacheKey, Object value) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheObject", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("value", value);
        }}).map(response -> {
            response.throwExceptionIfNotSuccess();
            return response.succeeded();
        }).toCompletable();
    }

    /**
     * set the object
     *
     * @param cacheKey        the key of the cache
     * @param value           the value
     * @param timeoutInSecond time out in seconds
     * @return complete or error
     */
    public static Completable set(String cacheKey, Object value, int timeoutInSecond) {
        return set(CacheService.CACHE_CONFIG_BEAN, cacheKey, value, timeoutInSecond);
    }

    /**
     * set the object
     *
     * @param cacheConfigBean the data source of the cache
     * @param cacheKey        the key of the cache
     * @param value           the value
     * @param timeoutInSecond time out in seconds
     * @return complete or error
     */
    public static Completable set(CacheConfigBean cacheConfigBean, String cacheKey, Object value, int timeoutInSecond) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheObject", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("value", value);
            put("timeout", timeoutInSecond);
        }}).map(unitResponse -> {
            unitResponse.throwExceptionIfNotSuccess();
            return unitResponse.succeeded();
        }).toCompletable();
    }

    /**
     * get the cached object.
     *
     * @param cacheKey the key for the cache
     * @param clazz    the class
     * @param <T>      the type
     * @return single event observable for the cached object.
     */
    public static <T> Single<T> get(String cacheKey, Class<T> clazz) {
        return get(CacheService.CACHE_CONFIG_BEAN, cacheKey, clazz);
    }

    /**
     * get the cached object.
     *
     * @param cacheConfigBean the data source of the cache
     * @param cacheKey        the key for the cache
     * @param clazz           the class
     * @param <T>             the type
     * @return single event observable for the cached object.
     */
    public static <T> Single<T> get(CacheConfigBean cacheConfigBean, String cacheKey, Class<T> clazz) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheObjectGet", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }}).map(unitResponseObject -> {
            unitResponseObject.throwExceptionIfNotSuccess();
            return Reflection.toType(unitResponseObject.getData(), clazz);
        });
    }

    /**
     * remove the cached object.
     *
     * @param cacheKey the key of the cache
     * @return the single observable for the result, true on success, false if nothing changed, error on exception.
     */
    public static Single<Boolean> remove(String cacheKey) {
        return remove(CacheService.CACHE_CONFIG_BEAN, cacheKey);
    }

    /**
     * remove the cached object.
     *
     * @param cacheConfigBean the data source configuration
     * @param cacheKey        the key of the cache
     * @return the single observable for the result, true on success, false if nothing changed, error on exception.
     */
    public static Single<Boolean> remove(CacheConfigBean cacheConfigBean, String cacheKey) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheObjectRemove", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }}).map(unitResponseObject -> {
            unitResponseObject.throwExceptionIfNotSuccess();
            return unitResponseObject.succeeded();
        });
    }

    /**
     * Increase the specified cache by 1.
     *
     * @param cacheKey the key
     * @return the result value after increment, or error.
     */
    public static Single<Long> increment(String cacheKey) {
        return increment(CacheService.CACHE_CONFIG_BEAN, cacheKey);
    }

    /**
     * Increase the specified cache by 1.
     *
     * @param cacheConfigBean the datasource
     * @param cacheKey        the key
     * @return the result value after increment, or error.
     */
    public static Single<Long> increment(CacheConfigBean cacheConfigBean, String cacheKey) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheIncrement", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }}).map(unitResponse -> {
            unitResponse.throwExceptionIfNotSuccess();
            return unitResponse.dataToLong();
        });
    }

    /**
     * increase with specified increment
     *
     * @param cacheKey the key
     * @param value    the increment
     * @return the result value after increment, or error.
     */
    public static Single<Long> incrementByValue(String cacheKey, long value) {
        return incrementByValue(CacheService.CACHE_CONFIG_BEAN, cacheKey, value);
    }

    /**
     * increase with specified increment
     *
     * @param cacheConfigBean the datasource configuration
     * @param cacheKey        the key
     * @param value           the increment
     * @return the result value after increment, or error.
     */
    public static Single<Long> incrementByValue(CacheConfigBean cacheConfigBean, String cacheKey, long value) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheIncrement", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("value", value);
        }}).map(unitResponseObject -> {
            unitResponseObject.throwExceptionIfNotSuccess();
            return (long) unitResponseObject.getData();
        });
    }

    /**
     * increase with specified increment
     *
     * @param cacheKey        the key
     * @param value           the increment
     * @param timeoutInSecond time out in seconds
     * @return the result value after increment, or error.
     */
    public static Single<Long> incrementByValue(String cacheKey, long value, int timeoutInSecond) {
        return incrementByValue(CacheService.CACHE_CONFIG_BEAN, cacheKey, value, timeoutInSecond);
    }

    /**
     * increase with specified increment
     *
     * @param cacheConfigBean datasource configuration
     * @param cacheKey        the key
     * @param value           the increment
     * @param timeoutInSecond time out in seconds
     * @return the result value after increment, or error.
     */
    public static Single<Long> incrementByValue(CacheConfigBean cacheConfigBean, String cacheKey, long value, int timeoutInSecond) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheIncrement", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("value", value);
            put("timeout", timeoutInSecond);
        }}).map(response -> {
            response.throwExceptionIfNotSuccess();
            return (long) response.getData();
        });
    }

    /**
     * decrement
     *
     * @param cacheKey key
     * @return the result value after decrement, or error.
     */
    public static Single<Long> decrement(String cacheKey) {
        return decrement(CacheService.CACHE_CONFIG_BEAN, cacheKey);
    }

    /**
     * decrement
     *
     * @param cacheConfigBean datasource configuration
     * @param cacheKey        key
     * @return the result value after decrement, or error.
     */
    public static Single<Long> decrement(CacheConfigBean cacheConfigBean, String cacheKey) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheDecrement", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }}).map(response -> {
            response.throwExceptionIfNotSuccess();
            return (long) response.getData();
        });
    }

    /**
     * decrement
     *
     * @param cacheKey key
     * @param value    decrement
     * @return the result value after decrement, or error.
     */
    public static Single<Long> decrementByValue(String cacheKey, long value) {
        return decrementByValue(CacheService.CACHE_CONFIG_BEAN, cacheKey, value);
    }

    /**
     * decrement
     *
     * @param cacheConfigBean datasource configuration
     * @param cacheKey        key
     * @param value           decrement
     * @return the result value after decrement, or error.
     */
    public static Single<Long> decrementByValue(CacheConfigBean cacheConfigBean, String cacheKey, long value) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheDecrement", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("value", value);
        }}).map(unitResponseObject -> {
            unitResponseObject.throwExceptionIfNotSuccess();
            return (long) unitResponseObject.getData();
        });
    }

    /**
     * decrement
     *
     * @param cacheKey        key
     * @param value           decrement
     * @param timeoutInSecond time out in seconds
     * @return the result value after decrement, or error.
     */
    public static Single<Long> decrementByValue(String cacheKey, long value, int timeoutInSecond) {
        return decrementByValue(CacheService.CACHE_CONFIG_BEAN, cacheKey, value, timeoutInSecond);
    }

    /**
     * decrement
     *
     * @param cacheConfigBean datasource configuration
     * @param cacheKey        key
     * @param value           decrement
     * @param timeoutInSecond time out in seconds
     * @return the result value after decrement, or error.
     */
    public static Single<Long> decrementByValue(CacheConfigBean cacheConfigBean, String cacheKey, long value, int timeoutInSecond) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheDecrement", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("value", value);
            put("timeout", timeoutInSecond);
        }}).map(unitResponseObject -> {
            unitResponseObject.throwExceptionIfNotSuccess();
            return (long) unitResponseObject.getData();
        });
    }

    /**
     * @param cacheConfigBean datasource configuration
     * @param cacheKey        key
     * @return time to live in seconds
     */
    public static Single<Long> ttl(CacheConfigBean cacheConfigBean, String cacheKey) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheTtl", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }}).map(unitResponse -> {
            unitResponse.throwExceptionIfNotSuccess();
            return (long) unitResponse.getData();
        });
    }

    /**
     * @param cacheConfigBean datasource configuration
     * @param cacheKey        key
     * @return the cache type name
     */
    public static Single<String> type(CacheConfigBean cacheConfigBean, String cacheKey) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheType", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }}).map(unitResponseObject -> {
            unitResponseObject.throwExceptionIfNotSuccess();
            return (String) unitResponseObject.getData();
        });
    }

    /**
     * @param pattern pattern
     * @param count   count
     * @param cursor  "0" 开始新一轮迭代
     * @return cursor "0" 结束完成迭代
     * @deprecated 不建议使用该 API, 各种不稳定
     */
    public static Single<ScanVo> scan(String pattern, int count, String cursor) {
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
    public static Single<ScanVo> scan(CacheConfigBean cacheConfigBean, String pattern, int count, String cursor) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheScan", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("pattern", pattern);
            put("count", count);
            put("cursor", cursor);
        }}).map(unitResponseObject -> {
            unitResponseObject.throwExceptionIfNotSuccess();
            JSONObject jsonObject = unitResponseObject.dataToJson();
            return new ScanVo(jsonObject);
        });
    }

}
