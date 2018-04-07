package info.xiancloud.grafana.unit.dashboard;

import info.xiancloud.core.*;
import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.HttpUtil;
import info.xiancloud.grafana.GrafanaService;
import info.xiancloud.grafana.utils.GrafanaUtil;

import java.util.Map;

/**
 * get grafana dashboard
 */
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
        return UnitMeta.createWithDescription("获取单个 dashboard");
    }

    @Override
    public Input getInput() {
        return new Input().add("slug", String.class, "", REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        String slug = msg.get("slug", String.class);

        Map<String, String> headers = GrafanaUtil.gainHttpHeaders();

        String url = XianConfig.get("grafana_http_api_dashboards_db_url");
        HttpUtil
                .get(url + (url.endsWith("/") ? "" : "/") + slug, headers)
                .subscribe(response -> handler.handle(UnitResponse.createSuccess(response)));
    }

}
