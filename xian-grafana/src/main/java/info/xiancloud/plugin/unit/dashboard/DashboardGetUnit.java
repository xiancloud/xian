package info.xiancloud.plugin.unit.dashboard;

import info.xiancloud.plugin.*;
import info.xiancloud.plugin.conf.EnvConfig;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.util.HttpUtil;
import info.xiancloud.plugin.utils.GrafanaUtil;

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
            String url = EnvConfig.get("grafana_http_api_dashboards_db_url");
            String response = HttpUtil.get(url + (url.endsWith("/") ? "" : "/") + slug, headers);
            return UnitResponse.success(response);
        } catch (Exception e) {
            return UnitResponse.exception(e);
        }
    }

}
