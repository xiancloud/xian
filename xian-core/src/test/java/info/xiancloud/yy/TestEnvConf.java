package info.xiancloud.yy;

import info.xiancloud.plugin.conf.EnvConfig;
import info.xiancloud.plugin.util.EnvUtil;
import org.junit.Test;

/**
 * @author happyyangyuan
 * test config reader
 */
public class TestEnvConf {
    @Test
    public void extConfGetBooleanValueOfNoConfig() {
        System.out.println(EnvConfig.getBoolValue("ff"));
        System.out.println(EnvUtil.isRemoteSenderDisabled());
        System.out.println(EnvConfig.getBoolValue("applicationRemoteSenderDisabled"));
        System.out.println(EnvConfig.get(EnvUtil.LAN_REFERENCE_HOST_CONFIG, EnvUtil.DEFAULT_LAN_REFERENCE_HOST));
        System.out.println(System.getenv("gelfInputInternetUrl"));
        System.out.println(EnvConfig.get("gelfInputInternetUrl"));
        System.out.println(EnvUtil.isLan() ? EnvConfig.get("gelfInputLanUrl") : EnvConfig.get("gelfInputInternetUrl"));
    }
}
