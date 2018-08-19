package info.xiancloud.yy.application_config;

import info.xiancloud.core.conf.application.ApplicationConfig;

/**
 * @author happyyangyuan
 */
public class TestApplicationConfig {
    public static void main(String[] args) {
        System.out.println(ApplicationConfig.SINGLETON.get0("api_gateway_port"));
    }
}
