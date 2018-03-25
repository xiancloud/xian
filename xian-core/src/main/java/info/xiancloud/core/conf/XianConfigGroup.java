package info.xiancloud.core.conf;

import info.xiancloud.core.Group;

/**
 * @author happyyangyuan
 */
public class XianConfigGroup implements Group {
    @Override
    public String getName() {
        return "xianConfig";
    }

    @Override
    public String getDescription() {
        return "支持多环境的配置文件读取服务";
    }

    public static final XianConfigGroup singleton = new XianConfigGroup();
}
