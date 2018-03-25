package info.xiancloud.grafana.unit.dashboard;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.HttpUtil;
import info.xiancloud.grafana.GrafanaService;
import info.xiancloud.grafana.utils.GrafanaUtil;

import java.util.Map;

public class DashboardGetUnit implements Unit {
    @Override
    public String getName() {
        return "dashboardGet";
    }

    @Override
    public Group getGroup() {
        return GrafanaService.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("获取单个 dashboard");
    }

    @Override
    public Input getInput() {
        return new Input().add("slug", String.class, "", REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String slug = msg.get("slug", String.class);

        Map<String, String> headers = GrafanaUtil.gainHttpHeaders();

        try {
            String url = XianConfig.get("grafana_http_api_dashboards_db_url");
            String response = HttpUtil.get(url + (url.endsWith("/") ? "" : "/") + slug, headers);
            return UnitResponse.success(response);
        } catch (Exception e) {
            return UnitResponse.exception(e);
        }
    }

}
