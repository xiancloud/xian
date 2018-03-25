package info.xiancloud.core.support.falcon;

import info.xiancloud.core.Group;

/**
 * @author happyyangyuan
 */
public class DiyMonitorGroup implements Group {
    @Override
    public String getName() {
        return "diyMonitor";
    }

    @Override
    public String getDescription() {
        return "自定义监控的服务";
    }

    public static final DiyMonitorGroup singleton = new DiyMonitorGroup();
}
