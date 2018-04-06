package info.xiancloud.core.support.cache.system;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.support.cache.CacheConfigBean;
import info.xiancloud.core.support.cache.CacheService;
import io.reactivex.Completable;

/**
 * Cache system util class
 */
public class CacheSystemUtil {

    private CacheSystemUtil() {
    }

    /**
     * Add a cache datasource into the cache pool.
     *
     * @param cacheConfigBean the datasource configuration.
     */
    public static Completable jedisPoolAdd(CacheConfigBean cacheConfigBean) {
        return SingleRxXian.call(CacheService.CACHE_SERVICE, "jedisPoolAdd", new JSONObject() {{
            put("host", cacheConfigBean.getHost());
            put("port", cacheConfigBean.getPort());
            put("password", cacheConfigBean.getPassword());
            put("dbIndex", cacheConfigBean.getDbIndex());
        }}).map(unitResponse -> {
            unitResponse.throwExceptionIfNotSuccess();
            return unitResponse.succeeded();
        }).toCompletable();
    }
}
