package info.xiancloud.toolkit;

import info.xiancloud.core.Group;

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
