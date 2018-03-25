package info.xiancloud.monitor;

import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.Xian;
import info.xiancloud.core.support.cache.CacheService;
import info.xiancloud.core.util.EnvUtil;

import java.util.HashMap;

public class MonitorTest {

    public static void main(String[] args) {
        UnitResponse unitResponseObject = Xian.call("diyMonitor", "jedisMonitor", new HashMap());
        System.out.println(unitResponseObject);
    }

    static {
        /**
         * 初始化缓存服务
         */
        CacheService.initCacheService(getUrl(), getPassword(), getDBIndex());

        CacheService.initCacheService("123.207.53.152:6379", "", getDBIndex());
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

    private static int getDBIndex() {
        return XianConfig.getIntValue("redisDbIndex", 0);
    }

}
