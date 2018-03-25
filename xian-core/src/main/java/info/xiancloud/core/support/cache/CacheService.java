package info.xiancloud.core.support.cache;

import info.xiancloud.core.message.SyncXian;

import java.util.HashMap;

/**
 * 缓存服务
 *
 * @author John_zero
 */
public final class CacheService {

    public static final String CACHE_SERVICE = "cache";

    public static final int DEFAULT_TIMEOUT = -1;

    public static final CacheConfigBean CACHE_CONFIG_BEAN = null; // new CacheConfigBean();

    /**
     * 初始化 缓存服务
     *
     * @param url      url
     * @param password password
     * @param dbIndex  dbIndex
     * @deprecated 不建议在redis插件外部初始化redis
     */
    public static void initCacheService(String url, String password, int dbIndex) {
        SyncXian.call(CACHE_SERVICE, "jedisInit", new HashMap<String, Object>() {{
            put("url", url);
            put("password", password);
            put("dbIndex", dbIndex);
        }});
    }

}
