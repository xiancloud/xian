package info.xiancloud.message.unit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.message.MessageGroup;
import info.xiancloud.message.short_msg.yunpian.JavaSmsApi;

/**
 * @author happyyangyuan
 */
public class SendShortMsg implements Unit {
    @Override
    public String getName() {
        return "sendShortMsg";
    }

    @Override
    public Group getGroup() {
        return MessageGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("发送短消息").setPublic(true);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("text", String.class, "短信内容", REQUIRED)
                .add("mobile", String.class, "手机号", REQUIRED);
    }

    @Override
    public void execute(UnitRequest unitRequest, Handler<UnitResponse> handler) {
        String text = unitRequest.getArgMap().get("text").toString(),
                mobile = unitRequest.getArgMap().get("mobile").toString();
        JavaSmsApi
                .sendSms("79b70a43235834e4b06c2feb4793a3d2", text, mobile)
                .subscribe(result -> {
                    String code = Group.CODE_UNKNOWN_ERROR;
                    JSONObject resultJson = JSON.parseObject(result);
                    if (resultJson.getIntValue("code") != 0) {
                        result = resultJson.getString("detail");
                    } else {
                        code = Group.CODE_SUCCESS;
                    }
                    handler.handle(UnitResponse.create(code, result, null));
                });
    }
}
