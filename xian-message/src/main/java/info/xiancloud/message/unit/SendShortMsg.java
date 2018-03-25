package info.xiancloud.message.unit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.message.MessageGroup;
import info.xiancloud.message.short_msg.yunpian.JavaSmsApi;

import java.io.IOException;

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
        return UnitMeta.create("发送短消息").setPublic(true);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("text", String.class, "短信内容", REQUIRED)
                .add("mobile", String.class, "手机号", REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest unitRequest) {
        String text = unitRequest.getArgMap().get("text").toString(),
                mobile = unitRequest.getArgMap().get("mobile").toString();
        String result = "消息发送失败,请重试.",
                code = Group.CODE_OPERATE_ERROR;
        try {
            result = JavaSmsApi.sendSms("79b70a43235834e4b06c2feb4793a3d2", text, mobile);
            JSONObject resultJson = JSON.parseObject(result);
            if (resultJson.getIntValue("code") != 0) {
                result = resultJson.getString("detail");
            } else {
                code = Group.CODE_SUCCESS;
            }
        } catch (IOException e) {
            LOG.error("短信发送失败异常", e);
            //this exception should not cause transactional rollback
        }
        return UnitResponse.create(code, result, null);
    }
}
