package info.xiancloud.core.support.cache.api;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;
import info.xiancloud.core.support.cache.CacheService;
import info.xiancloud.core.util.Reflection;
import io.reactivex.Completable;
import io.reactivex.Maybe;
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

    public static <T> Maybe<T> get(String key, String field, Class<T> clazz) {
        return get(CacheService.CACHE_CONFIG_BEAN, key, field, clazz);
    }

    public static <T> Maybe<T> get(CacheConfigBean cacheConfigBean, String key, String field, Class<T> clazz) {
        return SingleRxXian
                .call(CacheService.CACHE_SERVICE, "cacheMapGet", new JSONObject() {{
                    put("cacheConfig", cacheConfigBean);
                    put("key", key);
                    put("field", field);
                }})
                .flatMapMaybe(unitResponse -> {
                            unitResponse.throwExceptionIfNotSuccess();
                            if (unitResponse.getData() == null)
                                return Maybe.empty();
                            else
                                return Maybe.just(Reflection.toType(unitResponse.getData(), clazz));
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

    /**
     * retrival the cached map
     *
     * @param key    the key
     * @param kClazz the key's class
     * @param vClazz the values's class
     * @param <K>    the generic type of the key
     * @param <V>    the generic type of the value
     * @return the whole map or null if the key does not exist
     */
    public static <K, V> Maybe<Map<K, V>> getAll(String key, Class<K> kClazz, Class<V> vClazz) {
        return getAll(CacheService.CACHE_CONFIG_BEAN, key, kClazz, vClazz);
    }

    /**
     * retrival the cached map
     *
     * @param cacheConfigBean the datasource configuration of the cache
     * @param key             the key
     * @param kClazz          the key's class
     * @param vClazz          the values's class
     * @param <K>             the generic type of the key
     * @param <V>             the generic type of the value
     * @return the whole map
     */
    @SuppressWarnings("all")
    public static <K, V> Maybe<Map<K, V>> getAll(CacheConfigBean cacheConfigBean, String key, Class<K> kClazz, Class<V> vClazz) {
        return SingleRxXian
                .call(CacheService.CACHE_SERVICE, "cacheMapGetAll", new JSONObject() {{
                    put("cacheConfig", cacheConfigBean);
                    put("key", key);
                }})
                .flatMapMaybe(unitResponse -> {
                    unitResponse.throwExceptionIfNotSuccess();
                    if (unitResponse.getData() == null)
                        return Maybe.empty();
                    else {
                        JSONObject map = unitResponse.dataToJson();
                        if (map == null || map.isEmpty())
                            return Maybe.just(new HashMap<>());
                        Map<K, V> maps = new HashMap<>();
                        map.forEach((_key, _value) -> maps.put(Reflection.toType(_key, kClazz), Reflection.toType(_value, vClazz)));
                        return Maybe.just(maps);
                    }
                });
    }

    /**
     * @param key   the cache key
     * @param field the field in the map
     * @return true if any elements are removed. False if no fields are removed, which means the field is not in the map originally.
     */
    public static Single<Boolean> remove(String key, String field) {
        return remove(CacheService.CACHE_CONFIG_BEAN, key, field);
    }

    /**
     * remove the elements in the map
     *
     * @param cacheConfigBean the datasource configuration
     * @param key             the key
     * @param field           the field
     * @return true if any elements are removed. False if no fields are removed, which means the field is not in the map originally.
     */
    public static Single<Boolean> remove(CacheConfigBean cacheConfigBean, String key, String field) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheMapRemove", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
            put("field", field);
        }}).map(unitResponse -> {
            unitResponse.throwExceptionIfNotSuccess();
            long result = Objects.requireNonNull(unitResponse.dataToLong());
            return result > 0;
        });
    }

    /**
     * batch remove the elements in the cached map
     *
     * @param batchRemoves Map(key, fields) the sub map you want to remvoe
     */
    @SuppressWarnings("unused")
    public static Completable batchRemove(Map<String, List<String>> batchRemoves) {
        return batchRemove(CacheService.CACHE_CONFIG_BEAN, batchRemoves);
    }

    /**
     * batch remove the elements in the cached map
     *
     * @param cacheConfigBean cacheConfigBean
     * @param batchRemoves    Map(key, fields) the sub map you want to remvoe
     */
    @SuppressWarnings("all")
    public static Completable batchRemove(CacheConfigBean cacheConfigBean, Map<String, List<String>> batchRemoves) {
        return SingleRxXian
                .call(CacheService.CACHE_SERVICE, "cacheMapBatchRemove", new JSONObject() {{
                    put("cacheConfig", cacheConfigBean);
                    put("batchRemoves", batchRemoves);
                }})
                .map(UnitResponse::throwExceptionIfNotSuccess)
                .toCompletable();
    }

    /**
     * clear the cached map
     *
     * @param key the key
     * @return true if any elements are removed. false if no keys are removed, usually this is because the map is empty already.
     */
    public static Single<Boolean> clear(String key) {
        return clear(CacheService.CACHE_CONFIG_BEAN, key);
    }

    /**
     * clear the cached map
     *
     * @param cacheConfigBean the datasource configuration
     * @param key             the cache key
     * @return true if any elements are cleared, false if no keys are cleared.
     */
    public static Single<Boolean> clear(CacheConfigBean cacheConfigBean, String key) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheMapClear", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
        }}).map(unitResponse -> {
            unitResponse.throwExceptionIfNotSuccess();
            long result = Objects.requireNonNull(unitResponse.dataToLong());
            return result > 0;
        });
    }

    public static Maybe<Set<String>> keys(String key) {
        return keys(CacheService.CACHE_CONFIG_BEAN, key);
    }

    /**
     * retrial all the keys of the cached map
     *
     * @param cacheConfigBean datasource configuration
     * @param key             cache key
     * @return all the keys of the cached map, or null if the key does not exist
     */
    public static Maybe<Set<String>> keys(CacheConfigBean cacheConfigBean, String key) {
        return SingleRxXian
                .call(CacheService.CACHE_SERVICE, "cacheMapKeys", new JSONObject() {{
                    put("cacheConfig", cacheConfigBean);
                    put("key", key);
                }})
                .flatMapMaybe(unitResponse -> {
                    unitResponse.throwExceptionIfNotSuccess();
                    if (unitResponse.getData() == null)
                        return Maybe.empty();
                    else
                        return Maybe.just(Reflection.toTypedSet(unitResponse.getData(), String.class));
                });
    }

    /**
     * retrial all the keys of the cached map
     *
     * @param key    cache key
     * @param kClazz the key's class
     * @return all the keys of the cached map, or null if the key does not exist
     */
    public static <T> Maybe<Set<T>> keys(String key, Class<T> kClazz) {
        return keys(CacheService.CACHE_CONFIG_BEAN, key, kClazz);
    }

    /**
     * retrial all the keys of the cached map
     *
     * @param cacheConfigBean datasource configuration
     * @param key             cache key
     * @param kClazz          the key's class
     * @return all the keys of the cached map, or null if the key does not exist
     */
    public static <T> Maybe<Set<T>> keys(CacheConfigBean cacheConfigBean, String key, Class<T> kClazz) {
        return keys(cacheConfigBean, key)
                .map(keySet -> {
                    Set<T> _keys = new TreeSet<>();
                    keySet.forEach(_key -> _keys.add(Reflection.toType(_key, kClazz)));
                    return _keys;
                });
    }

    /**
     * @param key the cached list's key
     * @return the whole list if exists or null if the key does not exist.
     */
    public static Maybe<List<String>> values(String key) {
        return values(CacheService.CACHE_CONFIG_BEAN, key);
    }

    /**
     * retrieval all the values in the cache list
     *
     * @return the whole list if exists or null if the key does not exist.
     */
    public static Maybe<List<String>> values(CacheConfigBean cacheConfigBean, String key) {
        return SingleRxXian
                .call(CacheService.CACHE_SERVICE, "cacheMapValues", new JSONObject() {{
                    put("cacheConfig", cacheConfigBean);
                    put("key", key);
                }})
                .flatMapMaybe(unitResponse -> {
                    unitResponse.throwExceptionIfNotSuccess();
                    if (unitResponse.getData() == null)
                        return Maybe.empty();
                    else
                        return Maybe.just(Reflection.toTypedList(unitResponse.getData(), String.class));
                });
    }

    /**
     * retrieval all the values in the cache map
     *
     * @param key    the key of the cached map
     * @param vClazz the value's class
     * @param <T>    the value's generic type
     * @return the whole list if exists or null if the key does not exist.return
     */
    public static <T> Maybe<List<T>> values(String key, Class<T> vClazz) {
        return values(CacheService.CACHE_CONFIG_BEAN, key, vClazz);
    }

    /**
     * retrieval all the values in the cached map
     *
     * @param cacheConfigBean datasource configuration of the cache system
     * @param key             the key of the cache
     * @param vClazz          the element class
     * @param <T>             the generic type of the list
     * @return the whole list if exists or null if the key does not exist.return
     */
    public static <T> Maybe<List<T>> values(CacheConfigBean cacheConfigBean, String key, Class<T> vClazz) {
        /*
        return values(cacheConfigBean, key);
        It is a bad idea to call values(cacheConfigBean,key) to get a string list, because this may cause a waste of
        serialization and deserialization.
        */
        return SingleRxXian
                .call(CacheService.CACHE_SERVICE, "cacheMapValues", new JSONObject() {{
                    put("cacheConfig", cacheConfigBean);
                    put("key", key);
                }})
                .flatMapMaybe(unitResponse -> {
                    unitResponse.throwExceptionIfNotSuccess();
                    if (unitResponse.getData() == null)
                        return Maybe.empty();
                    else
                        return Maybe.just(Reflection.toTypedList(unitResponse.getData(), vClazz));
                });
    }

}
