package info.xiancloud.core.support.cache.api;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.message.SyncXian;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;
import info.xiancloud.core.support.cache.CacheService;
import info.xiancloud.core.util.Reflection;

import java.util.*;

/**
 * Map 结构
 *
 * @author John_zero
 */
public final class CacheMapUtil {

    private CacheMapUtil() {
    }

    public static boolean exists(String key) {
        return exists(CacheService.CACHE_CONFIG_BEAN, key);
    }

    public static boolean exists(CacheConfigBean cacheConfigBean, String key) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheMapExists", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();

        return (boolean) unitResponseObject.getData();
    }

    @Deprecated
    public static boolean exists(String key, String field) {
        return exists(CacheService.CACHE_CONFIG_BEAN, key, field);
    }

    @Deprecated
    public static boolean exists(CacheConfigBean cacheConfigBean, String key, String field) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheMapExists", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
            put("field", field);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();

        return (boolean) unitResponseObject.getData();
    }

    public static boolean containsKey(String key, String field) {
        return containsKey(CacheService.CACHE_CONFIG_BEAN, key, field);
    }

    public static boolean containsKey(CacheConfigBean cacheConfigBean, String key, String field) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheMapExists", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
            put("field", field);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();

        return (boolean) unitResponseObject.getData();
    }

    public static long size(String key) {
        return size(CacheService.CACHE_CONFIG_BEAN, key);
    }

    public static long size(CacheConfigBean cacheConfigBean, String key) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheMapSize", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();

        long size = (long) unitResponseObject.getData();
        return size > 0 ? size : 0;
    }

    public static boolean isEmpty(String key) {
        return isEmpty(CacheService.CACHE_CONFIG_BEAN, key);
    }

    public static boolean isEmpty(CacheConfigBean cacheConfigBean, String key) {
        long size = size(cacheConfigBean, key);
        if (size == 0)
            return true;
        else
            return false;
    }

    public static <T> T put(String key, String field, T value) {
        return put(CacheService.CACHE_CONFIG_BEAN, key, field, value, CacheService.DEFAULT_TIMEOUT);
    }

    public static <T> T put(CacheConfigBean cacheConfigBean, String key, String field, T value) {
        return put(cacheConfigBean, key, field, value, CacheService.DEFAULT_TIMEOUT);
    }

    @Deprecated
    public static <T> T put(String key, String field, T value, int seconds) {
        return put(CacheService.CACHE_CONFIG_BEAN, key, field, value, seconds);
    }

    @Deprecated
    public static <T> T put(CacheConfigBean cacheConfigBean, String key, String field, T value, int seconds) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheMapPut", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
            put("field", field);
            put("value", value);
            put("timeout", seconds);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();

        if (unitResponseObject.succeeded())
            return value;
        else
            return null;
    }

    public static void putAll(String key, Map maps) {
        putAll(CacheService.CACHE_CONFIG_BEAN, key, maps);
    }

    public static void putAll(CacheConfigBean cacheConfigBean, String key, Map maps) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheMapPutAll", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
            put("maps", maps);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();
    }

    public static <T> T get(String key, String field, Class<T> clazz) {
        return get(CacheService.CACHE_CONFIG_BEAN, key, field, clazz);
    }

    public static <T> T get(CacheConfigBean cacheConfigBean, String key, String field, Class<T> clazz) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheMapGet", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
            put("field", field);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();

        return Reflection.toType(unitResponseObject.getData(), clazz);
    }

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

    public static <K, V> Map<K, V> getAll(String key, Class<K> kClazz, Class<V> vClazz) {
        return getAll(CacheService.CACHE_CONFIG_BEAN, key, kClazz, vClazz);
    }

    public static <K, V> Map<K, V> getAll(CacheConfigBean cacheConfigBean, String key, Class<K> kClazz, Class<V> vClazz) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheMapGetAll", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();

        Map<String, String> map = null;
        if (unitResponseObject.getData() != null)
            map = Map.class.cast(unitResponseObject.getData());

        if (map == null || map.isEmpty())
            return new HashMap<>();

        Map<K, V> maps = new HashMap<>();
        map.forEach((_key, _value) -> {
            maps.put(kClazz.cast(_key), Reflection.toType(_value, vClazz));
        });

        return maps;
    }

    public static boolean remove(String key, String field) {
        return remove(CacheService.CACHE_CONFIG_BEAN, key, field);
    }

    public static boolean remove(CacheConfigBean cacheConfigBean, String key, String field) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheMapRemove", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
            put("field", field);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();

        long result = (long) unitResponseObject.getData();
        if (result > 0)
            return true;
        else
            return false;
    }

    public static void batchRemove(Map<String, List<String>> batchRemoves) {
        batchRemove(CacheService.CACHE_CONFIG_BEAN, batchRemoves);
    }

    /**
     * @param cacheConfigBean cacheConfigBean
     * @param batchRemoves    Map(key, fields)
     */
    public static void batchRemove(CacheConfigBean cacheConfigBean, Map<String, List<String>> batchRemoves) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheMapBatchRemove", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("batchRemoves", batchRemoves);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();
    }

    public static boolean clear(String key) {
        return clear(CacheService.CACHE_CONFIG_BEAN, key);
    }

    public static boolean clear(CacheConfigBean cacheConfigBean, String key) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheMapClear", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();

        long result = (long) unitResponseObject.getData();
        if (result > 0)
            return true;
        else
            return false;
    }

    @Deprecated
    public static Set<String> keys(String key) {
        return keys(CacheService.CACHE_CONFIG_BEAN, key);
    }

    @Deprecated
    public static Set<String> keys(CacheConfigBean cacheConfigBean, String key) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheMapKeys", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();

        if (unitResponseObject.getData() != null)
            return (Set<String>) unitResponseObject.getData();
        else
            return null;
    }

    public static <T> Set<T> keys(String key, Class<T> kClazz) {
        return keys(CacheService.CACHE_CONFIG_BEAN, key, kClazz);
    }

    public static <T> Set<T> keys(CacheConfigBean cacheConfigBean, String key, Class<T> kClazz) {
        Set<String> keys = keys(cacheConfigBean, key);

        Set<T> _keys = new TreeSet<>();
        keys.forEach(_key -> {
            _keys.add(Reflection.toType(_key, kClazz));
        });

        return _keys;
    }

    @Deprecated
    public static List<String> values(String key) {
        return values(CacheService.CACHE_CONFIG_BEAN, key);
    }

    public static List<String> values(CacheConfigBean cacheConfigBean, String key) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheMapValues", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();
        return unitResponseObject.getData();
    }

    public static <T> List<T> values(String key, Class<T> vClazz) {
        return values(CacheService.CACHE_CONFIG_BEAN, key, vClazz);
    }

    public static <T> List<T> values(CacheConfigBean cacheConfigBean, String key, Class<T> vClazz) {
        List<T> lists = new ArrayList<>();

        List<String> collection = values(cacheConfigBean, key);
        if (collection != null && !collection.isEmpty()) {
            collection.forEach(json -> {
                lists.add(Reflection.toType(json, vClazz));
            });
        }

        return lists;
    }

}
