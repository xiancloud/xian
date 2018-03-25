package info.xiancloud.yy;

import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.util.EnvUtil;
import org.junit.Test;

/**
 * @author happyyangyuan
 * test config reader
 */
public class TestEnvConf {
    @Test
    public void extConfGetBooleanValueOfNoConfig() {
        System.out.println(XianConfig.getBoolValue("ff"));
        System.out.println(EnvUtil.isRemoteSenderDisabled());
        System.out.println(XianConfig.getBoolValue("applicationRemoteSenderDisabled"));
        System.out.println(XianConfig.get(EnvUtil.LAN_REFERENCE_HOST_CONFIG, EnvUtil.DEFAULT_LAN_REFERENCE_HOST));
        System.out.println(System.getenv("gelfInputInternetUrl"));
        System.out.println(XianConfig.get("gelfInputInternetUrl"));
        System.out.println(EnvUtil.isLan() ? XianConfig.get("gelfInputLanUrl") : XianConfig.get("gelfInputInternetUrl"));
    }
}
