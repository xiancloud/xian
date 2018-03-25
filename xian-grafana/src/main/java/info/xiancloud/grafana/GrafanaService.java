package info.xiancloud.grafana;

import info.xiancloud.core.Group;

public class GrafanaService implements Group {

    public static Group singleton = new GrafanaService();

    @Override
    public String getName() {
        return "grafanaService";
    }

    @Override
    public String getDescription() {
        return "Grafana 服务";
    }

}
