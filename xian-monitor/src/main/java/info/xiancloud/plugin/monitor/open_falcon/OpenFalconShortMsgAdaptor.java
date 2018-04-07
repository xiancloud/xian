package info.xiancloud.plugin.monitor.open_falcon;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.*;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.thread.ListenableCountDownLatch;
import info.xiancloud.plugin.monitor.common.MonitorGroup;

/**
 * @author happyyangyuan
 */
public class OpenFalconShortMsgAdaptor implements Unit {
    @Override
    public String getName() {
        return "openFalconShortMsg";
    }

    @Override
    public Group getGroup() {
        return MonitorGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("适配openFalcon的短信发送接口");
    }

    /**
     * openFalcon官方文档：
     * method: post
     * params:
     * - content: 短信内容
     * - tos: 使用逗号分隔的多个手机号
     */
    public Input getInput() {
        return new Input()
                .add("content", String.class, "短信内容", REQUIRED)
                .add("tos", String.class, "使用逗号分隔的多个手机号", REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        String[] mobiles = msg.argJson().getString("tos").split(",");
        ListenableCountDownLatch countDownLatch = new ListenableCountDownLatch(mobiles.length);
        for (String mobile : mobiles) {
            SingleRxXian.call("message", "sendShortMsg", new JSONObject() {{
                put("text", msg.get("content", String.class));
                put("mobile", mobile);
            }}).subscribe(unitResponse -> {
                LOG.info("发送给" + mobile + "的短信发送结果:" + unitResponse);
                countDownLatch.countDown();
            });
        }
        countDownLatch.addListener(counter -> {
            if (counter == 0) {
                handler.handle(UnitResponse.createSuccess());
            }
        });
    }
}
