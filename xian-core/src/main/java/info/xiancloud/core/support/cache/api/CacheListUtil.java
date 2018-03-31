package info.xiancloud.core.support.cache.api;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.AsyncResult;
import info.xiancloud.core.message.Xian;
import info.xiancloud.core.support.cache.CacheConfigBean;
import info.xiancloud.core.support.cache.CacheService;
import info.xiancloud.core.util.Reflection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * cache List operation asynchronous util.
 *
 * @author John_zero, happyyangyuan
 */
public final class CacheListUtil {

    private CacheListUtil() {
    }

    @Deprecated
    public static void exists(String cacheKey, Consumer<AsyncResult<Boolean>> consumer) {
        exists(CacheService.CACHE_CONFIG_BEAN, cacheKey, consumer);
    }

    @Deprecated
    public static void exists(CacheConfigBean cacheConfigBean, String cacheKey, Consumer<AsyncResult<Boolean>> consumer) {
        Xian.call(CacheService.CACHE_SERVICE, "cacheListExists", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }}, unitResponse -> consumer.accept(AsyncResult.fromUnitResponse(unitResponse)));
    }

    public static void length(String cacheKey, Consumer<AsyncResult<Long>> consumer) {
        length(CacheService.CACHE_CONFIG_BEAN, cacheKey, consumer);
    }

    public static void length(CacheConfigBean cacheConfigBean, String cacheKey, Consumer<AsyncResult<Long>> consumer) {
        Xian.call(CacheService.CACHE_SERVICE, "cacheListLength", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }}, unitResponse -> consumer.accept(AsyncResult.fromUnitResponse(unitResponse, () -> {
            long length = unitResponse.getData();
            return length > 0 ? length : 0;
        })));
    }

    public static void isEmpty(String cacheKey, Consumer<AsyncResult<Boolean>> consumer) {
        isEmpty(CacheService.CACHE_CONFIG_BEAN, cacheKey, consumer);
    }

    public static void isEmpty(CacheConfigBean cacheConfigBean, String cacheKey, Consumer<AsyncResult<Boolean>> consumer) {
        length(cacheConfigBean, cacheKey, asyncResult -> {
            if (asyncResult.succeeded())
                consumer.accept(AsyncResult.from(asyncResult, () -> asyncResult.result() == 0));
        });
    }

    public static void addHead(String cacheKey, Object value, Consumer<AsyncResult<Boolean>> consumer) {
        addHead(CacheService.CACHE_CONFIG_BEAN, cacheKey, value, consumer);
    }

    /**
     * append element to the list's head.
     *
     * @param cacheConfigBean cacheConfigBean
     * @param cacheKey        cacheKey
     * @param value           value
     * @param consumer        true on success, false on failure.
     */
    public static void addHead(CacheConfigBean cacheConfigBean, String cacheKey, Object value, Consumer<AsyncResult<Boolean>> consumer) {
        Xian.call(CacheService.CACHE_SERVICE, "cacheListAddHead", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("valueObj", value);
        }}, unitResponse -> consumer.accept(AsyncResult.fromUnitResponse(unitResponse, unitResponse::succeeded)));
    }

    public static void add(String cacheKey, Object value, Consumer<AsyncResult<Boolean>> consumer) {
        add(CacheService.CACHE_CONFIG_BEAN, cacheKey, value, consumer);
    }

    /**
     * 尾部追加
     *
     * @param cacheConfigBean cacheConfigBean
     * @param cacheKey        cacheKey
     * @param value           value
     * @param consumer        true on success, false on faulure.
     */
    public static void add(CacheConfigBean cacheConfigBean, String cacheKey, Object value, Consumer<AsyncResult<Boolean>> consumer) {
        Xian.call(CacheService.CACHE_SERVICE, "cacheListAdd", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("valueObj", value);
        }}, unitResponse -> consumer.accept(AsyncResult.fromUnitResponse(unitResponse, unitResponse::succeeded)));
    }

    public static void addAll(String cacheKey, List list, Consumer<AsyncResult<Boolean>> consumer) {
        addAll(CacheService.CACHE_CONFIG_BEAN, cacheKey, list, consumer);
    }

    public static void addAll(CacheConfigBean cacheConfigBean, String cacheKey, List list, Consumer<AsyncResult<Boolean>> consumer) {
        Xian.call(CacheService.CACHE_SERVICE, "cacheListAddAll", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("values", list);
        }}, unitResponse -> consumer.accept(AsyncResult.fromUnitResponse(unitResponse, unitResponse::succeeded)));
    }

    public static void set(String cacheKey, int index, Object value, Consumer<AsyncResult<Boolean>> consumer) {
        set(CacheService.CACHE_CONFIG_BEAN, cacheKey, index, value, consumer);
    }

    public static void set(CacheConfigBean cacheConfigBean, String cacheKey, int index, Object value, Consumer<AsyncResult<Boolean>> consumer) {
        Xian.call(CacheService.CACHE_SERVICE, "cacheListSet", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("index", index);
            put("valueObj", value);
        }}, unitResponse -> consumer.accept(AsyncResult.fromUnitResponse(unitResponse, unitResponse::succeeded)));
    }

    public static void remove(String cacheKey, Object value, Consumer<AsyncResult<Boolean>> consumer) {
        remove(CacheService.CACHE_CONFIG_BEAN, cacheKey, value, consumer);
    }

    public static void remove(CacheConfigBean cacheConfigBean, String cacheKey, Object value, Consumer<AsyncResult<Boolean>> consumer) {
        Xian.call(CacheService.CACHE_SERVICE, "cacheListRemove", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("valueObj", value);
        }}, unitResponse -> consumer.accept(AsyncResult.fromUnitResponse(unitResponse, () -> {
            long result = unitResponse.getData();
            return result > 0;
        })));
    }

    public static void clear(String cacheKey, Consumer<AsyncResult<Boolean>> consumer) {
        clear(CacheService.CACHE_CONFIG_BEAN, cacheKey, consumer);
    }

    public static void clear(CacheConfigBean cacheConfigBean, String cacheKey, Consumer<AsyncResult<Boolean>> consumer) {
        Xian.call(CacheService.CACHE_SERVICE, "cacheListClear", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }}, unitResponse -> consumer.accept(AsyncResult.fromUnitResponse(unitResponse, unitResponse::succeeded)));
    }

    public static void delete(String cacheKey, Consumer<AsyncResult<Boolean>> consumer) {
        delete(CacheService.CACHE_CONFIG_BEAN, cacheKey, consumer);
    }

    public static void delete(CacheConfigBean cacheConfigBean, String cacheKey, Consumer<AsyncResult<Boolean>> consumer) {
        Xian.call(CacheService.CACHE_SERVICE, "cacheObjectRemove", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
        }}, unitResponse -> consumer.accept(AsyncResult.fromUnitResponse(unitResponse, unitResponse::succeeded)));
    }

    @Deprecated
    public static void getAll(String cacheKey, Consumer<AsyncResult<List>> consumer) {
        getRange(CacheService.CACHE_CONFIG_BEAN, cacheKey, 0, -1, consumer);
    }

    @Deprecated
    public static void getAll(CacheConfigBean cacheConfigBean, String cacheKey, Consumer<AsyncResult<List>> consumer) {
        getRange(cacheConfigBean, cacheKey, 0, -1, consumer);
    }

    @Deprecated
    public static void getRange(String cacheKey, int startIndex, int endIndex, Consumer<AsyncResult<List>> consumer) {
        getRange(CacheService.CACHE_CONFIG_BEAN, cacheKey, startIndex, endIndex, consumer);
    }

    /**
     * @return 返回list，如果key不存在，那么返回null，如果key为空列表，那么返回size=0的ArrayList
     */
    public static <T> void getAll(String cacheKey, Class<T> clazz, Consumer<AsyncResult<List<T>>> consumer) {
        Xian.call(CacheService.CACHE_SERVICE, "cacheListGetAll", new JSONObject() {{
            put("key", cacheKey);
        }}, unitResponse -> consumer.accept(AsyncResult.fromUnitResponse(unitResponse, () -> unitResponse.dataToTypedList(clazz))));
    }

    /**
     * 指定获取范围 (不推荐使用)
     */
    @Deprecated
    public static void getRange(CacheConfigBean cacheConfigBean, String cacheKey, int startIndex, int endIndex, Consumer<AsyncResult<List>> consumer) {
        Xian.call(CacheService.CACHE_SERVICE, "cacheListRange", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("startIndex", startIndex);
            put("endIndex", endIndex);
        }}, unitResponse -> consumer.accept(AsyncResult.fromUnitResponse(unitResponse, () -> {
            if (unitResponse.getData() != null)
                return unitResponse.getData();
            else
                return null;
        })));
    }

    public static <T> void getRange(String cacheKey, Class<T> clazz, Consumer<AsyncResult<List<T>>> consumer) {
        getRange(CacheService.CACHE_CONFIG_BEAN, cacheKey, 0, -1, clazz, consumer);
    }

    public static <T> void getRange(CacheConfigBean cacheConfigBean, String cacheKey, Class<T> clazz, Consumer<AsyncResult<List<T>>> consumer) {
        getRange(cacheConfigBean, cacheKey, 0, -1, clazz, consumer);
    }

    @Deprecated
    public static <T> void getRange(String cacheKey, int startIndex, int endIndex, Class<T> clazz, Consumer<AsyncResult<List<T>>> consumer) {
        getRange(CacheService.CACHE_CONFIG_BEAN, cacheKey, startIndex, endIndex, clazz, consumer);
    }

    /**
     * 指定获取范围
     *
     * @deprecated (不推荐使用)
     */
    public static <T> void getRange(CacheConfigBean cacheConfigBean, String cacheKey, int startIndex, int endIndex, Class<T> clazz, Consumer<AsyncResult<List<T>>> consumer) {
        getRange(cacheConfigBean, cacheKey, startIndex, endIndex, listAsyncResult -> consumer.accept(AsyncResult.from(listAsyncResult, () -> {
            List<String> lists = listAsyncResult.result();
            List<T> _lists = new ArrayList<>();
            lists.forEach(value -> _lists.add(Reflection.toType(value, clazz)));
            return _lists;
        })));
    }

    public static <T> void get(String cacheKey, int index, Class<T> clazz, Consumer<AsyncResult<T>> consumer) {
        get(CacheService.CACHE_CONFIG_BEAN, cacheKey, index, clazz, consumer);
    }

    public static <T> void get(CacheConfigBean cacheConfigBean, String cacheKey, int index, Class<T> clazz, Consumer<AsyncResult<T>> consumer) {
        Xian.call(CacheService.CACHE_SERVICE, "cacheListGetByIndex", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", cacheKey);
            put("index", index);
        }}, unitResponse -> consumer.accept(AsyncResult.fromUnitResponse(unitResponse, () -> {
            if (unitResponse.getData() == null)
                return null;
            return Reflection.toType(unitResponse.getData(), clazz);
        })));
    }

}
