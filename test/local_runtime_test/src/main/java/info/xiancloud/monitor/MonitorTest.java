package info.xiancloud.monitor;

import info.xiancloud.plugin.conf.XianConfig;
import info.xiancloud.plugin.message.Xian;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.support.cache.CacheService;
import info.xiancloud.plugin.util.EnvUtil;

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
        if (EnvUtil.isQcloudLan())
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
