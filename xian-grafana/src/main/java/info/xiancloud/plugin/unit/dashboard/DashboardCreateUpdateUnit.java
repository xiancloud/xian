package info.xiancloud.plugin.unit.dashboard;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.*;
import info.xiancloud.plugin.conf.XianConfig;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.util.HttpUtil;
import info.xiancloud.plugin.utils.GrafanaUtil;

import java.util.Map;

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
        return UnitMeta.create("创建, 更新");
    }

    @Override
    public Input getInput() {
        return new Input();
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        Map<String, String> headers = GrafanaUtil.gainHttpHeaders();
        try {
            String response = HttpUtil.post(XianConfig.get("grafana_http_api_dashboards_db_url"), msg.argJson().toString(), headers);
            JSONObject responseJosn = JSONObject.parseObject(response);
            if (responseJosn.containsKey("status")) {
                if (responseJosn.getString("status").equals("success"))
                    return UnitResponse.success(response);
            }
            return UnitResponse.failure(response, null);
        } catch (Exception e) {
            return UnitResponse.exception(e);
        }
    }

}
