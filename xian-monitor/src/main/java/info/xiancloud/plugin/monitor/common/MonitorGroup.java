package info.xiancloud.plugin.monitor.common;

import info.xiancloud.plugin.Group;

/**
 * @author happyyangyuan
 */
public class MonitorGroup implements Group {
    @Override
    public String getName() {
        return "monitor";
    }

    @Override
    public String getDescription() {
        return "系统监控服务";
    }

    public static final MonitorGroup singleton = new MonitorGroup();
}
