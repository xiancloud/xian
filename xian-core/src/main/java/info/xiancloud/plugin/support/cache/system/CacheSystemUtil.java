package info.xiancloud.plugin.support.cache.system;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.message.SyncXian;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.support.cache.CacheConfigBean;
import info.xiancloud.plugin.support.cache.CacheService;

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
    public static void jedisPoolAdd(CacheConfigBean cacheConfigBean) {
        UnitResponse unitResponseObject = SyncXian.call(CacheService.CACHE_SERVICE, "jedisPoolAdd", new JSONObject() {{
            put("host", cacheConfigBean.getHost());
            put("port", cacheConfigBean.getPort());
            put("password", cacheConfigBean.getPassword());
            put("dbIndex", cacheConfigBean.getDbIndex());
        }});
        unitResponseObject.throwExceptionIfNotSuccess();
    }

}
