package info.xiancloud.plugin.monitor.open_falcon;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.*;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.ArrayUtil;
import info.xiancloud.core.util.LOG;
import info.xiancloud.plugin.monitor.common.MonitorGroup;

import java.util.List;

/**
 * @author happyyangyuan
 */
public class OpenFalconMailAdaptor implements Unit {
    @Override
    public String getName() {
        return "openFalconMail";
    }

    @Override
    public Group getGroup() {
        return MonitorGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("适配openFalcon邮件发送接口");
    }

    /**
     * 来自open-falcon官方文档说明：
     * method: post
     * params:
     * - content: 邮件内容
     * - subject: 邮件标题
     * - tos: 使用逗号分隔的多个邮件地址
     */
    @Override
    public Input getInput() {
        return new Input()
                .add("content", String.class, "邮件内容")
                .add("subject", String.class, "邮件标题")
                .add("tos", String.class, "使用逗号分隔的多个邮件地址", REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        List<String> recipients = ArrayUtil.toList(msg.get("tos", String.class).split(","), String.class);
        SingleRxXian.call("message", "sendEmail", new JSONObject() {{
            put("recipients", recipients);
            put("subject", msg.get("subject", String.class));
            put("content", msg.get("content", String.class));
        }}).subscribe(unitResponse -> {
            LOG.info("邮件发送结果：" + unitResponse);
            handler.handle(unitResponse);
        });
    }
}
