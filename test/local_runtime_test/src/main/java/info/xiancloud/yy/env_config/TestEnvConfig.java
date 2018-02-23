package info.xiancloud.yy.env_config;

import info.xiancloud.plugin.conf.EnvConfig;

import java.util.Arrays;

/**
 * @author happyyangyuan
 */
public class TestEnvConfig {
    public static void main(String[] args) {
        System.out.println("rabbitmqUserName=   " + EnvConfig.get("rabbitmqUserName"));
        System.out.println("redisLanUrl=   " + Arrays.toString(EnvConfig.getStringArray("redisLanUrl")));
        System.out.println(EnvConfig.getIntValue("redisDbIndex"));
    }
}
