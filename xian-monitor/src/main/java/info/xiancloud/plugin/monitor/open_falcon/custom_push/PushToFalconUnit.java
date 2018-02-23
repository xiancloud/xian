package info.xiancloud.plugin.monitor.open_falcon.custom_push;

import com.alibaba.fastjson.JSONArray;
import info.xiancloud.plugin.*;
import info.xiancloud.plugin.monitor.common.MonitorGroup;
import info.xiancloud.plugin.conf.EnvConfig;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.socket.ConnectTimeoutException;
import info.xiancloud.plugin.util.EnvUtil;
import info.xiancloud.plugin.util.HttpUtil;

import java.net.SocketTimeoutException;

/**
 * 推送监控数据unit
 *
 * @author happyyangyuan
 */
public class PushToFalconUnit implements Unit {
    @Override
    public String getName() {
        return "pushToFalcon";
    }

    @Override
    public Group getGroup() {
        return MonitorGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("向falcon推送数据，建议以异步方式调用此unit");
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("metric", String.class, "指标名", REQUIRED)
                .add("value", Object.class, "数值/JSONObject/JSONArray", REQUIRED)
                .add("step", int.class, "汇报周期，单位秒", REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        FalconBeanBuilder builder = new FalconBeanBuilder();
        builder.setStep(msg.get("step", int.class));
        JSONArray falconBeans = new JSONArray() {{
            addAll(builder.buildAll(msg.argJson().getString("metric"), msg.getArgMap().get("value")));
        }};
        try {
            HttpUtil.postWithEmptyHeader(
                    EnvUtil.isLan() ? EnvConfig.get("qCloud.falcon_transfer_url") : EnvConfig.get("internet.falcon_transfer_url"),
                    falconBeans.toJSONString());
        } catch (SocketTimeoutException | ConnectTimeoutException e) {
            return UnitResponse.exception(e);
        }
        return UnitResponse.success();
    }
}
