package info.xiancloud.grafana.unit.dashboard;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.*;
import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.HttpUtil;
import info.xiancloud.grafana.GrafanaService;
import info.xiancloud.grafana.utils.GrafanaUtil;

import java.util.Map;

/**
 * @author John_zero, happyyangyuan
 */
public class DashboardCreateUpdateUnit implements Unit {

    @Override
    public String getName() {
        return "dashboardCreateUpdate";
    }

    @Override
    public Group getGroup() {
        return GrafanaService.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("创建, 更新").setDocApi(false);
    }

    @Override
    public Input getInput() {
        return new Input();
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        Map<String, String> headers = GrafanaUtil.gainHttpHeaders();
        HttpUtil
                .post(XianConfig.get("grafana_http_api_dashboards_db_url"), msg.argJson().toString(), headers)
                .subscribe(response -> {
                    JSONObject responseJson = JSONObject.parseObject(response);
                    if (responseJson.containsKey("status")) {
                        if (responseJson.getString("status").equals("success")) {
                            handler.handle(UnitResponse.createSuccess(response));
                            return;
                        }
                    }
                    handler.handle(UnitResponse.createUnknownError(response, null));
                });
    }

}
