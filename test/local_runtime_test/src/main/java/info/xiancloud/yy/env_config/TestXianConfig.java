package info.xiancloud.yy.env_config;

import info.xiancloud.plugin.conf.XianConfig;

import java.util.Arrays;

/**
 * @author happyyangyuan
 */
public class TestXianConfig {
    public static void main(String[] args) {
        System.out.println("rabbitmqUserName=   " + XianConfig.get("rabbitmqUserName"));
        System.out.println("redisLanUrl=   " + Arrays.toString(XianConfig.getStringArray("redisLanUrl")));
        System.out.println(XianConfig.getIntValue("redisDbIndex"));
    }
}
