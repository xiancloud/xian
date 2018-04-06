package info.xiancloud.plugin.monitor.open_falcon.custom_push;

import com.alibaba.fastjson.JSONArray;
import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.socket.ConnectTimeoutException;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.HttpUtil;
import info.xiancloud.plugin.monitor.common.MonitorGroup;

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
        return UnitMeta.createWithDescription("向falcon推送数据，建议以异步方式调用此unit");
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
                    EnvUtil.isLan() ? XianConfig.get("qCloud.falcon_transfer_url") : XianConfig.get("internet.falcon_transfer_url"),
                    falconBeans.toJSONString());
        } catch (SocketTimeoutException | ConnectTimeoutException e) {
            return UnitResponse.createException(e);
        }
        return UnitResponse.createSuccess();
    }
}
