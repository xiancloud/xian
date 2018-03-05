package info.xiancloud.redis;

import info.xiancloud.plugin.conf.XianConfig;
import info.xiancloud.plugin.support.cache.CacheService;
import info.xiancloud.plugin.util.EnvUtil;

/**
 * @author John_zero
 */
public abstract class BaseRedis {
    public BaseRedis() {

    }

    static {
        /*
         * 初始化缓存服务
         */
        CacheService.initCacheService(getUrl(), getPassword(), getDBIndex());
    }

    private static String getUrl() {
        if (EnvUtil.isQcloudLan())
            return XianConfig.get("redisLanUrl"); // 腾讯云内网内
        else
            return XianConfig.get("redisInternetUrl"); // 外网
    }

    private static String getPassword() {
        return XianConfig.get("redisPassword");
    }

    protected static int getDBIndex() {
        return XianConfig.getIntValue("redisDbIndex", 0);
    }

}
