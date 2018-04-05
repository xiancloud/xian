package info.xiancloud.core.support.cache.api;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;
import info.xiancloud.core.support.cache.CacheService;
import info.xiancloud.core.util.Reflection;
import io.reactivex.Completable;
import io.reactivex.Single;

import java.util.*;

/**
 * cached Map operation
 *
 * @author John_zero, happyyangyuan
 */
public final class CacheMapUtil {

    private CacheMapUtil() {
    }

    public static Single<Boolean> exists(String key) {
        return exists(CacheService.CACHE_CONFIG_BEAN, key);
    }

    public static Single<Boolean> exists(CacheConfigBean cacheConfigBean, String key) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheMapExists", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
        }}).map(unitResponse -> {
            unitResponse.throwExceptionIfNotSuccess();
            return Objects.requireNonNull(unitResponse.dataToBoolean());
        });
    }

    public static Single<Boolean> containsKey(String key, String field) {
        return containsKey(CacheService.CACHE_CONFIG_BEAN, key, field);
    }

    public static Single<Boolean> containsKey(CacheConfigBean cacheConfigBean, String key, String field) {
        return SingleRxXian
                .call(CacheService.CACHE_SERVICE, "cacheMapExists", new JSONObject() {{
                    put("cacheConfig", cacheConfigBean);
                    put("key", key);
                    put("field", field);
                }})
                .map(unitResponse -> {
                    unitResponse.throwExceptionIfNotSuccess();
                    return Objects.requireNonNull(unitResponse.dataToBoolean());
                });
    }

    public static Single<Long> size(String key) {
        return size(CacheService.CACHE_CONFIG_BEAN, key);
    }

    public static Single<Long> size(CacheConfigBean cacheConfigBean, String key) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheMapSize", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
        }}).map(unitResponse -> {
            unitResponse.throwExceptionIfNotSuccess();
            Long size = unitResponse.dataToLong();
            return size != null && size > 0 ? size : 0;
        });
    }

    public static Single<Boolean> isEmpty(String key) {
        return isEmpty(CacheService.CACHE_CONFIG_BEAN, key);
    }

    public static Single<Boolean> isEmpty(CacheConfigBean cacheConfigBean, String key) {
        return size(cacheConfigBean, key)
                .map(size -> size == 0);
    }

    public static <T> Single<Boolean> put(String key, String field, T value) {
        return put(CacheService.CACHE_CONFIG_BEAN, key, field, value);
    }

    /**
     * cache map put
     *
     * @param cacheConfigBean the cache data source configuration
     * @param key             the cache key
     * @param field           the field of the cache map.
     * @param value           the value you want to put it in the map.
     * @param <T>             the generic type of the value.
     * @return the async result, true for success, false if failure.
     */
    public static <T> Single<Boolean> put(CacheConfigBean cacheConfigBean, String key, String field, T value) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheMapPut", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
            put("field", field);
            put("value", value);
        }}).map(unitResponse -> {
            unitResponse.throwExceptionIfNotSuccess();
            return unitResponse.succeeded();
        });
    }

    public static Completable putAll(String key, Map maps) {
        return putAll(CacheService.CACHE_CONFIG_BEAN, key, maps);
    }

    public static Completable putAll(CacheConfigBean cacheConfigBean, String key, Map maps) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheMapPutAll", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
            put("maps", maps);
        }}).toCompletable();
    }

    public static <T> Single<T> get(String key, String field, Class<T> clazz) {
        return get(CacheService.CACHE_CONFIG_BEAN, key, field, clazz);
    }

    public static <T> Single<T> get(CacheConfigBean cacheConfigBean, String key, String field, Class<T> clazz) {
        return SingleRxXian
                .call(CacheService.CACHE_SERVICE, "cacheMapGet", new JSONObject() {{
                    put("cacheConfig", cacheConfigBean);
                    put("key", key);
                    put("field", field);
                }})
                .map(unitResponse -> {
                            unitResponse.throwExceptionIfNotSuccess();
                            return Reflection.toType(unitResponse.getData(), clazz);
                        }
                );
    }

/*
    @Deprecated
    public static Map<String, String> getAll(String key) {
        return getAll(CacheService.CACHE_CONFIG_BEAN, key, String.class, String.class);
    }

    @Deprecated
    public static Map<String, String> getAll(CacheConfigBean cacheConfigBean, String key) {
        return getAll(cacheConfigBean, key, String.class, String.class);
    }

    @Deprecated
    public static <V> Map<String, V> getAll(String key, Class<V> vClazz) {
        return getAll(CacheService.CACHE_CONFIG_BEAN, key, String.class, vClazz);
    }

    @Deprecated
    public static <V> Map<String, V> getAll(CacheConfigBean cacheConfigBean, String key, Class<V> vClazz) {
        return getAll(cacheConfigBean, key, String.class, vClazz);
    }
*/

    public static <K, V> Single<Map<K, V>> getAll(String key, Class<K> kClazz, Class<V> vClazz) {
        return getAll(CacheService.CACHE_CONFIG_BEAN, key, kClazz, vClazz);
    }

    public static <K, V> Single<Map<K, V>> getAll(CacheConfigBean cacheConfigBean, String key, Class<K> kClazz, Class<V> vClazz) {
        return SingleRxXian
                .call(CacheService.CACHE_SERVICE, "cacheMapGetAll", new JSONObject() {{
                    put("cacheConfig", cacheConfigBean);
                    put("key", key);
                }})
                .map(unitResponse -> {
                    unitResponse.throwExceptionIfNotSuccess();
                    Map<String, String> map = null;
                    if (unitResponse.getData() != null)
                        map = Map.class.cast(unitResponse.getData());
                    if (map == null || map.isEmpty())
                        return new HashMap<>();
                    Map<K, V> maps = new HashMap<>();
                    map.forEach((_key, _value) -> {
                        maps.put(kClazz.cast(_key), Reflection.toType(_value, vClazz));
                    });
                    return maps;
                });
    }

    public static Single<Boolean> remove(String key, String field) {
        return remove(CacheService.CACHE_CONFIG_BEAN, key, field);
    }

    public static Single<Boolean> remove(CacheConfigBean cacheConfigBean, String key, String field) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheMapRemove", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
            put("field", field);
        }}).map(unitResponse -> {
            unitResponse.throwExceptionIfNotSuccess();
            long result = (long) unitResponse.getData();
            return result > 0;
        });
    }

    public static void batchRemove(Map<String, List<String>> batchRemoves) {
        batchRemove(CacheService.CACHE_CONFIG_BEAN, batchRemoves);
    }

    /**
     * @param cacheConfigBean cacheConfigBean
     * @param batchRemoves    Map(key, fields)
     */
    public static Completable batchRemove(CacheConfigBean cacheConfigBean, Map<String, List<String>> batchRemoves) {
        return SingleRxXian
                .call(CacheService.CACHE_SERVICE, "cacheMapBatchRemove", new JSONObject() {{
                    put("cacheConfig", cacheConfigBean);
                    put("batchRemoves", batchRemoves);
                }})
                .map(UnitResponse::throwExceptionIfNotSuccess)
                .toCompletable();
    }

    public static Single<Boolean> clear(String key) {
        return clear(CacheService.CACHE_CONFIG_BEAN, key);
    }

    public static Single<Boolean> clear(CacheConfigBean cacheConfigBean, String key) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheMapClear", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
        }}).map(unitResponse -> {
            unitResponse.throwExceptionIfNotSuccess();
            long result = (long) unitResponse.getData();
            return result > 0;
        });
    }

    public static Single<Set<String>> keys(String key) {
        return keys(CacheService.CACHE_CONFIG_BEAN, key);
    }

    @SuppressWarnings("unchecked")
    public static Single<Set<String>> keys(CacheConfigBean cacheConfigBean, String key) {
        return SingleRxXian
                .call(CacheService.CACHE_SERVICE, "cacheMapKeys", new JSONObject() {{
                    put("cacheConfig", cacheConfigBean);
                    put("key", key);
                }})
                .map(unitResponse -> {
                    unitResponse.throwExceptionIfNotSuccess();
                    return (Set<String>) unitResponse.getData();
                });
    }

    public static <T> Single<Set<T>> keys(String key, Class<T> kClazz) {
        return keys(CacheService.CACHE_CONFIG_BEAN, key, kClazz);
    }

    public static <T> Single<Set<T>> keys(CacheConfigBean cacheConfigBean, String key, Class<T> kClazz) {
        return keys(cacheConfigBean, key)
                .map(keySet -> {
                    Set<T> _keys = new TreeSet<>();
                    keySet.forEach(_key -> _keys.add(Reflection.toType(_key, kClazz)));
                    return _keys;
                });
    }

    public static Single<List<String>> values(String key) {
        return values(CacheService.CACHE_CONFIG_BEAN, key);
    }

    /**
     * retrieval the values in the cache list
     */
    @SuppressWarnings("unchecked")
    public static Single<List<String>> values(CacheConfigBean cacheConfigBean, String key) {
        return SingleRxXian
                .call(CacheService.CACHE_SERVICE, "cacheMapValues", new JSONObject() {{
                    put("cacheConfig", cacheConfigBean);
                    put("key", key);
                }})
                .map(unitResponse -> {
                    unitResponse.throwExceptionIfNotSuccess();
                    return (List<String>) unitResponse.getData();
                });
    }

    public static <T> Single<List<T>> values(String key, Class<T> vClazz) {
        return values(CacheService.CACHE_CONFIG_BEAN, key, vClazz);
    }

    public static <T> Single<List<T>> values(CacheConfigBean cacheConfigBean, String key, Class<T> vClazz) {
        List<T> lists = new ArrayList<>();
        return values(cacheConfigBean, key)
                .map(collection -> {
                    if (collection != null && !collection.isEmpty()) {
                        collection.forEach(json -> lists.add(Reflection.toType(json, vClazz)));
                    }
                    return lists;
                });
    }

}
