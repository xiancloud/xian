package info.xiancloud.plugin.conf;

import info.xiancloud.plugin.Group;

/**
 * @author happyyangyuan
 */
public class EnvConfigGroup implements Group {
    @Override
    public String getName() {
        return "envConfig";
    }

    @Override
    public String getDescription() {
        return "支持多环境的配置文件读取服务";
    }

    public static final EnvConfigGroup singleton = new EnvConfigGroup();
}
