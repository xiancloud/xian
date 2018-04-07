package info.xiancloud.core.support.cache.api;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;
import info.xiancloud.core.support.cache.CacheService;
import info.xiancloud.core.util.Reflection;
import io.reactivex.Completable;
import io.reactivex.Single;

import java.util.List;
import java.util.Objects;

/**
 * cache List operation asynchronous util.
 *
 * @author John_zero, happyyangyuan
 */
public final class CacheListUtil {

    private CacheListUtil() {
    }

    /**
     * check whether the list exists
     *
     * @param cacheKey the cache key
     * @return true if exists, other wise false
     */
    public static Single<Boolean> exists(String cacheKey) {
        return exists(CacheService.CACHE_CONFIG_BEAN, cacheKey);
    }

    /**
     * check whether the list exists
     *
     * @param cacheConfigBean the cache data source.
     * @param cacheKey        the cache key
     * @return true if exists, other wise false
     */
    public static Single<Boolean> exists(CacheConfigBean cacheConfigBean, String cacheKey) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheListExists", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }}).map(UnitResponse::dataToBoolean);
    }

    /**
     * query the length of the cached list
     *
     * @param cacheKey the cache key
     * @return the observable of the cache list length
     */
    public static Single<Long> length(String cacheKey) {
        return length(CacheService.CACHE_CONFIG_BEAN, cacheKey);
    }

    /**
     * query the length of the cached list
     *
     * @param cacheConfigBean the cache data source
     * @param cacheKey        the key for the cached list
     * @return the cached list's length
     */
    public static Single<Long> length(CacheConfigBean cacheConfigBean, String cacheKey) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheListLength", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }}).map(unitResponse -> {
            long length = Objects.requireNonNull(unitResponse.dataToLong());
            return length > 0 ? length : 0;
        });
    }

    /**
     * check whether the cached list is empty or not.
     *
     * @param cacheKey the key for the cached list
     * @return true is the cached list is empty or does not exists, other wise false.
     */
    public static Single<Boolean> isEmpty(String cacheKey) {
        return isEmpty(CacheService.CACHE_CONFIG_BEAN, cacheKey);
    }

    /**
     * check whether the cached list is empty or not.
     *
     * @param cacheConfigBean the data source of the cache
     * @param cacheKey        the key for the cached list
     * @return true is the cached list is empty or does not exists, other wise false.
     */
    public static Single<Boolean> isEmpty(CacheConfigBean cacheConfigBean, String cacheKey) {
        return length(cacheConfigBean, cacheKey).map(length -> length == 0);
    }

    /**
     * add element to the list's header
     *
     * @param cacheKey the key for the cached list.
     * @param value    the value to be added.
     */
    public static Single<Boolean> addFirst(String cacheKey, Object value) {
        return addFirst(CacheService.CACHE_CONFIG_BEAN, cacheKey, value);
    }

    /**
     * append element to the list's header.
     *
     * @param cacheConfigBean cacheConfigBean
     * @param cacheKey        cacheKey
     * @param value           value
     * @return true on success, false on failure.
     */
    @SuppressWarnings("all")
    public static Single<Boolean> addFirst(CacheConfigBean cacheConfigBean, String cacheKey, Object value) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheListAddHead", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("valueObj", value);
        }}).map(UnitResponse::succeeded);
    }

    /**
     * add an element to the end of the list
     *
     * @param cacheKey the key for the cached list.
     * @param value    the value you want it to be added.
     * @return true on success, false on failure.
     */
    public static Single<Boolean> add(String cacheKey, Object value) {
        return add(CacheService.CACHE_CONFIG_BEAN, cacheKey, value);
    }

    /**
     * add an element to the end of the list
     *
     * @param cacheConfigBean the data source of the cache system.
     * @param cacheKey        the key for the cached list.
     * @param value           the element you want to add.
     * @return true on success, false on failure.
     */
    public static Single<Boolean> add(CacheConfigBean cacheConfigBean, String cacheKey, Object value) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheListAdd", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("valueObj", value);
        }}).map(UnitResponse::succeeded);
    }

    /**
     * Add a list of elements to the end of the cached list.
     *
     * @param cacheKey the key for the cached list.
     * @param list     the list of elements to be added.
     * @return true on success, false on failure.
     */
    public static Single<Boolean> addAll(String cacheKey, List list) {
        return addAll(CacheService.CACHE_CONFIG_BEAN, cacheKey, list);
    }

    /**
     * Add a list of elements to the end of the cached list.
     *
     * @param cacheConfigBean the data source of the cache system.
     * @param cacheKey        the key for the cached list.
     * @param list            the list of elements to be added.
     * @return true on success, false on failure.
     */
    public static Single<Boolean> addAll(CacheConfigBean cacheConfigBean, String cacheKey, List list) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheListAddAll", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("values", list);
        }}).map(UnitResponse::succeeded);
    }

    /**
     * update the value of the specified index
     *
     * @param cacheKey the key for the cached list.
     * @param index    the index of the element you want to update.
     * @param value    the new value for the element.
     * @return true on success, false on failure.
     */
    public static Single<Boolean> set(String cacheKey, int index, Object value) {
        return set(CacheService.CACHE_CONFIG_BEAN, cacheKey, index, value);
    }

    /**
     * update the value of the specified index
     *
     * @param cacheConfigBean the data source of the cache system.
     * @param cacheKey        the key for the cached list.
     * @param index           the index of the element you want to update.
     * @param value           the new value for the element.
     * @return true on success, false on failure.
     */
    public static Single<Boolean> set(CacheConfigBean cacheConfigBean, String cacheKey, int index, Object value) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheListSet", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("index", index);
            put("valueObj", value);
        }}).map(UnitResponse::succeeded);
    }

    /**
     * remove the given element
     *
     * @param cacheKey the key for the cached list.
     * @param value    the element you want to remove.
     * @return the count of elements removed.
     */
    public static Single<Long> remove(String cacheKey, Object value) {
        return remove(CacheService.CACHE_CONFIG_BEAN, cacheKey, value);
    }

    /**
     * remove the given element
     *
     * @param cacheConfigBean the data source of the cache system.
     * @param cacheKey        the key for the cached list.
     * @param value           the element you want to remove.
     * @return the count of elements removed.
     */
    public static Single<Long> remove(CacheConfigBean cacheConfigBean, String cacheKey, Object value) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheListRemove", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("valueObj", value);
        }}).map(unitResponse -> {
            unitResponse.throwExceptionIfNotSuccess();
            return unitResponse.dataToLong();
        });
    }

    /**
     * clear the specified cached list.
     *
     * @param cacheKey the key for the cached list.
     * @return the completable which tells you this operation is completed.
     */
    public static Completable clear(String cacheKey) {
        return clear(CacheService.CACHE_CONFIG_BEAN, cacheKey);
    }

    /**
     * clear the specified cached list.
     *
     * @param cacheConfigBean the data source of the cache system.
     * @param cacheKey        the key for the cached list.
     * @return the completable which tells you this operation is completed.
     */
    public static Completable clear(CacheConfigBean cacheConfigBean, String cacheKey) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheListClear", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }}).toCompletable();
    }

    /**
     * delete the cached list
     *
     * @param cacheKey the key for the cached list.
     * @return false if nothing deleted, true if deleted successfully, error if operation failed.
     */
    public static Single<Boolean> delete(String cacheKey) {
        return delete(CacheService.CACHE_CONFIG_BEAN, cacheKey);
    }

    /**
     * delete the cached list
     *
     * @param cacheConfigBean the data source of the cache system.
     * @param cacheKey        the key for the cached list.
     * @return false if nothing deleted, true if deleted successfully.
     */
    public static Single<Boolean> delete(CacheConfigBean cacheConfigBean, String cacheKey) {
        return CacheObjectUtil.remove(cacheConfigBean, cacheKey);
    }

    public static Single<List<String>> getAll(String cacheKey) {
        return getRange(CacheService.CACHE_CONFIG_BEAN, cacheKey, 0, -1);
    }

    @SuppressWarnings("all")
    public static Single<List<String>> getAll(CacheConfigBean cacheConfigBean, String cacheKey) {
        return getRange(cacheConfigBean, cacheKey, 0, -1);
    }

    @SuppressWarnings("all")
    public static Single<List<String>> getRange(String cacheKey, int startIndex, int endIndex) {
        return getRange(CacheService.CACHE_CONFIG_BEAN, cacheKey, startIndex, endIndex);
    }

    /**
     * @return 返回list，如果key不存在，那么返回null，如果key为空列表，那么返回size=0的ArrayList
     */
    @SuppressWarnings("all")
    public static <T> Single<List<T>> getAll(String cacheKey, Class<T> clazz) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheListGetAll", new JSONObject() {{
            put("key", cacheKey);
        }}).map(unitResponse -> unitResponse.dataToTypedList(clazz));
    }

    /**
     * 指定获取范围 (返回的类型是raw string，所以不推荐使用)
     */
    @SuppressWarnings("all")
    public static Single<List<String>> getRange(CacheConfigBean cacheConfigBean, String cacheKey, int startIndex, int endIndex) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheListRange", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("startIndex", startIndex);
            put("endIndex", endIndex);
        }}).map(unitResponse -> {
            if (unitResponse.getData() != null)
                return unitResponse.dataToTypedList(String.class);
            else
                return null;
        });
    }

    public static <T> Single<List<T>> getRange(String cacheKey, Class<T> clazz) {
        return getRange(CacheService.CACHE_CONFIG_BEAN, cacheKey, 0, -1, clazz);
    }

    @SuppressWarnings("all")
    public static <T> Single<List<T>> getRange(CacheConfigBean cacheConfigBean, String cacheKey, Class<T> clazz) {
        return getRange(cacheConfigBean, cacheKey, 0, -1, clazz);
    }

    @SuppressWarnings("all")
    public static <T> Single<List<T>> getRange(String cacheKey, int startIndex, int endIndex, Class<T> clazz) {
        return getRange(CacheService.CACHE_CONFIG_BEAN, cacheKey, startIndex, endIndex, clazz);
    }

    /**
     * 指定获取范围
     */
    @SuppressWarnings("all")
    public static <T> Single<List<T>> getRange(CacheConfigBean cacheConfigBean, String cacheKey, int startIndex, int endIndex, Class<T> clazz) {
        return getRange(cacheConfigBean, cacheKey, startIndex, endIndex)
                .map(strings -> Reflection.toTypedList(strings, clazz));
    }

    public static <T> Single<T> get(String cacheKey, int index, Class<T> clazz) {
        return get(CacheService.CACHE_CONFIG_BEAN, cacheKey, index, clazz);
    }

    public static <T> Single<T> get(CacheConfigBean cacheConfigBean, String cacheKey, int index, Class<T> clazz) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheListGetByIndex", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("index", index);
        }}).map(unitResponse -> {
            if (unitResponse.getData() == null)
                return null;
            return Reflection.toType(unitResponse.getData(), clazz);
        });
    }

}
