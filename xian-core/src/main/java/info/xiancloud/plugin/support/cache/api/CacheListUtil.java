package info.xiancloud.plugin.support.cache.api;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.message.SyncXian;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.Xian;
import info.xiancloud.plugin.support.cache.CacheConfigBean;
import info.xiancloud.plugin.support.cache.CacheService;
import info.xiancloud.plugin.util.Reflection;

import java.util.ArrayList;
import java.util.List;

/**
 * cache List operation util.
 *
 * @author John_zero
 */
public final class CacheListUtil {

    private CacheListUtil() {
    }

    @Deprecated
    public static boolean exists(String cacheKey) {
        return exists(CacheService.CACHE_CONFIG_BEAN, cacheKey);
    }

    @Deprecated
    public static boolean exists(CacheConfigBean cacheConfigBean, String cacheKey) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheListExists", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();

        return (boolean) unitResponseObject.getData();
    }

    public static long length(String cacheKey) {
        return length(CacheService.CACHE_CONFIG_BEAN, cacheKey);
    }

    public static long length(CacheConfigBean cacheConfigBean, String cacheKey) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheListLength", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();

        long length = (long) unitResponseObject.getData();
        return length > 0 ? length : 0;
    }

    public static boolean isEmpty(String cacheKey) {
        return isEmpty(CacheService.CACHE_CONFIG_BEAN, cacheKey);
    }

    public static boolean isEmpty(CacheConfigBean cacheConfigBean, String cacheKey) {
        long length = length(cacheConfigBean, cacheKey);
        return length == 0;
    }

    public static boolean addHead(String cacheKey, Object value) {
        return addHead(CacheService.CACHE_CONFIG_BEAN, cacheKey, value);
    }

    /**
     * append element to the list's head.
     *
     * @param cacheConfigBean cacheConfigBean
     * @param cacheKey        cacheKey
     * @param value           value
     * @return true on success, false on failure.
     */
    public static boolean addHead(CacheConfigBean cacheConfigBean, String cacheKey, Object value) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheListAddHead", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("valueObj", value);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();

        if (unitResponseObject.succeeded())
            return true;
        else
            return false;
    }

    public static boolean add(String cacheKey, Object value) {
        return add(CacheService.CACHE_CONFIG_BEAN, cacheKey, value);
    }

    /**
     * 尾部追加
     *
     * @param cacheConfigBean cacheConfigBean
     * @param cacheKey        cacheKey
     * @param value           value
     * @return true on success, false on faulure.
     */
    public static boolean add(CacheConfigBean cacheConfigBean, String cacheKey, Object value) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheListAdd", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("valueObj", value);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();
        return unitResponseObject.succeeded();
    }

    public static boolean addAll(String cacheKey, List list) {
        return addAll(CacheService.CACHE_CONFIG_BEAN, cacheKey, list);
    }

    public static boolean addAll(CacheConfigBean cacheConfigBean, String cacheKey, List list) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheListAddAll", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("values", list);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();
        return unitResponseObject.succeeded();
    }

    public static boolean set(String cacheKey, int index, Object value) {
        return set(CacheService.CACHE_CONFIG_BEAN, cacheKey, index, value);
    }

    public static boolean set(CacheConfigBean cacheConfigBean, String cacheKey, int index, Object value) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheListSet", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("index", index);
            put("valueObj", value);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();
        return unitResponseObject.succeeded();
    }

    public static boolean remove(String cacheKey, Object value) {
        return remove(CacheService.CACHE_CONFIG_BEAN, cacheKey, value);
    }

    public static boolean remove(CacheConfigBean cacheConfigBean, String cacheKey, Object value) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheListRemove", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("valueObj", value);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();

        long result = (long) unitResponseObject.getData();
        if (result > 0)
            return true;
        else
            return false;
    }

    public static boolean clear(String cacheKey) {
        return clear(CacheService.CACHE_CONFIG_BEAN, cacheKey);
    }

    public static boolean clear(CacheConfigBean cacheConfigBean, String cacheKey) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheListClear", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();
        return unitResponseObject.succeeded();
    }

    public static boolean delete(String cacheKey) {
        return delete(CacheService.CACHE_CONFIG_BEAN, cacheKey);
    }

    public static boolean delete(CacheConfigBean cacheConfigBean, String cacheKey) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheObjectRemove", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();
        return unitResponseObject.succeeded();
    }

    @Deprecated
    public static List getAll(String cacheKey) {
        return getRange(CacheService.CACHE_CONFIG_BEAN, cacheKey, 0, -1);
    }

    @Deprecated
    public static List getAll(CacheConfigBean cacheConfigBean, String cacheKey) {
        return getRange(cacheConfigBean, cacheKey, 0, -1);
    }

    @Deprecated
    public static List getRange(String cacheKey, int startIndex, int endIndex) {
        return getRange(CacheService.CACHE_CONFIG_BEAN, cacheKey, startIndex, endIndex);
    }

    /**
     * @return 返回list，如果key不存在，那么返回null，如果key为空列表，那么返回size=0的ArrayList
     */
    public static <T> List<T> getAll(String cacheKey, Class<T> clazz) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheListGetAll", new JSONObject() {{
            put("key", cacheKey);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();
        return unitResponseObject.dataToTypedList(clazz);
    }

    /**
     * 指定获取范围 (不推荐使用)
     */
    @Deprecated
    public static List getRange(CacheConfigBean cacheConfigBean, String cacheKey, int startIndex, int endIndex) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheListRange", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("startIndex", startIndex);
            put("endIndex", endIndex);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();
        if (unitResponseObject.getData() != null)
            return unitResponseObject.getData();
        else
            return null;
    }

    public static <T> List<T> getRange(String cacheKey, Class<T> clazz) {
        return getRange(CacheService.CACHE_CONFIG_BEAN, cacheKey, 0, -1, clazz);
    }

    public static <T> List<T> getRange(CacheConfigBean cacheConfigBean, String cacheKey, Class<T> clazz) {
        return getRange(cacheConfigBean, cacheKey, 0, -1, clazz);
    }

    @Deprecated
    public static <T> List<T> getRange(String cacheKey, int startIndex, int endIndex, Class<T> clazz) {
        return getRange(CacheService.CACHE_CONFIG_BEAN, cacheKey, startIndex, endIndex, clazz);
    }

    /**
     * 指定获取范围 (推荐使用)
     */
    @Deprecated
    public static <T> List<T> getRange(CacheConfigBean cacheConfigBean, String cacheKey, int startIndex, int endIndex, Class<T> clazz) {
        List<String> lists = getRange(cacheConfigBean, cacheKey, startIndex, endIndex);

        List<T> _lists = new ArrayList<T>();
        lists.forEach(value -> {
            _lists.add(Reflection.toType(value, clazz));
        });

        return _lists;
    }

    public static <T> T get(String cacheKey, int index, Class<T> clazz) {
        return get(CacheService.CACHE_CONFIG_BEAN, cacheKey, index, clazz);
    }

    public static <T> T get(CacheConfigBean cacheConfigBean, String cacheKey, int index, Class<T> clazz) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheListGetByIndex", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("index", index);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();
        if (unitResponseObject.getData() == null)
            return null;
        return Reflection.toType(unitResponseObject.getData(), clazz);
    }

}
