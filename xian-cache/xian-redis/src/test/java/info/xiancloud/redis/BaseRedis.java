package info.xiancloud.redis;

import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.support.cache.CacheService;
import info.xiancloud.core.util.EnvUtil;

/**
 * @author John_zero, happyyangyuan
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
        if (EnvUtil.isLan())
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
