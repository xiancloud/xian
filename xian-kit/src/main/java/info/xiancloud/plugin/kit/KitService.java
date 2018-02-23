package info.xiancloud.plugin.kit;

import info.xiancloud.plugin.Group;

/**
 * 工具服务
 */
public class KitService implements Group {
    public static final Group singleton = new KitService();

    @Override
    public String getName() {
        return "kit";
    }
}
