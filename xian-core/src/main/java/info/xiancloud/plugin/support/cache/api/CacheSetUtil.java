package info.xiancloud.plugin.support.cache.api;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.message.SyncXian;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.support.cache.CacheConfigBean;
import info.xiancloud.plugin.support.cache.CacheService;
import info.xiancloud.plugin.util.Reflection;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * 缓存 SetUtil 无序集合
 *
 * @author John_zero
 */
public final class CacheSetUtil {

    private CacheSetUtil() {

    }

    public static boolean exists(String key, Object member) {
        return exists(CacheService.CACHE_CONFIG_BEAN, key, member);
    }

    public static boolean exists(CacheConfigBean cacheConfigBean, String key, Object member) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheSetSisMember", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
            put("member", member);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();

        return (boolean) unitResponseObject.getData();
    }

    public static long add(String key, Object member) {
        return add(CacheService.CACHE_CONFIG_BEAN, key, member);
    }

    public static long add(CacheConfigBean cacheConfigBean, String key, Object member) {
        Set members = new HashSet<>();
        members.add(member);

        return adds(cacheConfigBean, key, members);
    }

    public static long adds(String key, Set members) {
        return adds(CacheService.CACHE_CONFIG_BEAN, key, members);
    }

    public static long adds(CacheConfigBean cacheConfigBean, String key, Set members) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheSetAdd", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
            put("members", members);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();

        if (unitResponseObject.getData() == null)
            return 0L;

        return (long) unitResponseObject.getData();
    }

    public static Set<String> values(String key) {
        return values(CacheService.CACHE_CONFIG_BEAN, key);
    }

    public static Set<String> values(CacheConfigBean cacheConfigBean, String key) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "cacheSetMembers", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
        }});
        unitResponseObject.throwExceptionIfNotSuccess();
        return unitResponseObject.getData();
    }

    public static <T> Set<T> values(String key, Class<T> vClazz) {
        return values(CacheService.CACHE_CONFIG_BEAN, key, vClazz);
    }

    public static <T> Set<T> values(CacheConfigBean cacheConfigBean, String key, Class<T> vClazz) {
        Set<String> values = values(cacheConfigBean, key);

        Set<T> _values = new TreeSet<>();

        if (values != null && !values.isEmpty()) {
            values.forEach(value -> {
                _values.add(Reflection.toType(value, vClazz));
            });
        }

        return _values;
    }

    public final static long remove(String key, Object member) {
        return remove(CacheService.CACHE_CONFIG_BEAN, key, member);
    }

    public final static long remove(CacheConfigBean cacheConfigBean, String key, Object member) {
        Set members = new HashSet<>();
        members.add(member);

        return removes(cacheConfigBean, key, members);
    }

    public final static long removes(String key, Set members) {
        return removes(CacheService.CACHE_CONFIG_BEAN, key, members);
    }

    public final static long removes(CacheConfigBean cacheConfigBean, String key, Set members) {
        UnitResponse response = SyncXian.call(CacheService.CACHE_SERVICE, "cacheSetRemoves", new JSONObject() {{
            put("cacheConfig", cacheConfigBean);
            put("key", key);
            put("members", members);
        }});
        response.throwExceptionIfNotSuccess();
        return (long) response.getData();
    }

}
