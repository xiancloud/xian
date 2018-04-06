package info.xiancloud.core.support.cache;

import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitResponse;
import io.reactivex.Single;

import java.util.HashMap;

/**
 * cache service
 *
 * @author John_zero, happyyangyuan
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
     * @deprecated 不建议在redis插件外部初始化redis, for test only
     */
    public static Single<Boolean> initCacheService(String url, String password, int dbIndex) {
        return SingleRxXian.call(CACHE_SERVICE, "jedisInit", new HashMap<String, Object>() {{
            put("url", url);
            put("password", password);
            put("dbIndex", dbIndex);
        }}).map(UnitResponse::succeeded);
    }

}
