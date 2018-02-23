package info.xiancloud.yy.application_config;

import info.xiancloud.plugin.conf.application.ApplicationConfig;

/**
 * @author happyyangyuan
 */
public class TestApplicationConfig {
    public static void main(String[] args) {
        System.out.println(ApplicationConfig.singleton.get0("api_gateway_port"));
    }
}
