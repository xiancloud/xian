package info.xiancloud.core.support.cache.api;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.support.cache.CacheConfigBean;
import info.xiancloud.core.support.cache.CacheService;
import info.xiancloud.core.util.Reflection;
import io.reactivex.Single;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * cache Set Util class
 *
 * @author John_zero, happyyangyuan
 */
public final class CacheSetUtil {

    private CacheSetUtil() {
    }

    /**
     * check whether the element exists in the set
     *
     * @param key    key
     * @param member the element to check
     * @return true if existed, false otherwise.
     */
    public static Single<Boolean> exists(String key, Object member) {
        return exists(CacheService.CACHE_CONFIG_BEAN, key, member);
    }

    /**
     * check whether the element exists in the set
     *
     * @param cacheConfigBean the datasource configuration
     * @param key             the key
     * @param member          the element
     * @return true if existed, false otherwise.
     */
    public static Single<Boolean> exists(CacheConfigBean cacheConfigBean, String key, Object member) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheSetSisMember", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
            put("member", member);
        }}).map(unitResponse -> {
            unitResponse.throwExceptionIfNotSuccess();
            return (boolean) unitResponse.getData();
        });
    }

    /**
     * add the given element into the set.
     *
     * @param key    the key
     * @param member the element to be added
     * @return the count of elements added, 1 means success, 0 means no elements is added
     */
    public static Single<Long> add(String key, Object member) {
        return add(CacheService.CACHE_CONFIG_BEAN, key, member);
    }

    /**
     * @param cacheConfigBean the datasource configuration.
     * @param key             the key
     * @param member          the element to be added
     * @return the count of elements added, 1 means success, 0 means no elements is added
     */
    public static Single<Long> add(CacheConfigBean cacheConfigBean, String key, Object member) {
        Set<Object> members = new HashSet<>();
        members.add(member);
        return addAll(cacheConfigBean, key, members);
    }

    /**
     * add all the given elements into the set
     *
     * @param key     the key
     * @param members the elements
     * @return the added elements count
     */
    public static Single<Long> addAll(String key, Set members) {
        return addAll(CacheService.CACHE_CONFIG_BEAN, key, members);
    }

    /**
     * add all the given elements into the set
     *
     * @param cacheConfigBean the cache datasource configuration
     * @param key             key
     * @param members         elements to be added
     * @return the added elements count
     */
    public static Single<Long> addAll(CacheConfigBean cacheConfigBean, String key, Set members) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheSetAdd", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
            put("members", members);
        }}).map(unitResponseObject -> {
            unitResponseObject.throwExceptionIfNotSuccess();
            if (unitResponseObject.getData() == null)
                return 0L;
            return unitResponseObject.dataToLong();
        });
    }

    /**
     * retrial the cached set
     *
     * @param key key
     * @return the value set
     */
    public static Single<Set<String>> values(String key) {
        return values(CacheService.CACHE_CONFIG_BEAN, key);
    }

    /**
     * retrial the cached set
     *
     * @param cacheConfigBean the datasource configuration
     * @param key             the key
     * @return the value set
     */
    public static Single<Set<String>> values(CacheConfigBean cacheConfigBean, String key) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheSetMembers", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
        }}).map(unitResponseObject -> {
            unitResponseObject.throwExceptionIfNotSuccess();
            return Reflection.toTypedSet(unitResponseObject.getData(), String.class);
        });
    }

    /**
     * retrial the cached set
     *
     * @param key    key
     * @param vClazz value class
     * @param <T>    generic type
     * @return the value set
     */
    public static <T> Single<Set<T>> values(String key, Class<T> vClazz) {
        return values(CacheService.CACHE_CONFIG_BEAN, key, vClazz);
    }

    /**
     * retrial the cached set
     *
     * @param cacheConfigBean the datasource configuration
     * @param key             the key
     * @param vClazz          the class for the element
     * @param <T>             generic type of the set
     * @return the typed value set
     */
    public static <T> Single<Set<T>> values(CacheConfigBean cacheConfigBean, String key, Class<T> vClazz) {
        return values(cacheConfigBean, key)
                .map(values -> {
                    Set<T> _values = new TreeSet<>();
                    if (values != null && !values.isEmpty()) {
                        values.forEach(value -> {
                            _values.add(Reflection.toType(value, vClazz));
                        });
                    }
                    return _values;
                });
    }

    /**
     * remove the given element from the cache set
     *
     * @param key    key
     * @param member the element to remove
     * @return the removed elements count
     */
    public static Single<Long> remove(String key, Object member) {
        return remove(CacheService.CACHE_CONFIG_BEAN, key, member);
    }

    /**
     * remove the given element from the cache set
     *
     * @param cacheConfigBean datasource configuration
     * @param key             the key
     * @param member          element to be removed
     * @return the removed elements count
     */
    public static Single<Long> remove(CacheConfigBean cacheConfigBean, String key, Object member) {
        Set<Object> members = new HashSet<>();
        members.add(member);
        return removes(cacheConfigBean, key, members);
    }

    /**
     * remove the given elements from the cache set
     *
     * @param key     the key
     * @param members element to be removed
     * @return the removed elements count
     */
    public static Single<Long> removes(String key, Set members) {
        return removes(CacheService.CACHE_CONFIG_BEAN, key, members);
    }

    /**
     * remove the given element from the cache set
     *
     * @param cacheConfigBean datasource configuration
     * @param key             the key
     * @param members         elements to be removed
     * @return the removed elements count
     */
    public static Single<Long> removes(CacheConfigBean cacheConfigBean, String key, Set members) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "cacheSetRemoves", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
            put("members", members);
        }}).map(response -> {
            response.throwExceptionIfNotSuccess();
            return response.dataToLong();
        });
    }

}
