package com.apifest.oauth20.unit.client;

import com.alibaba.fastjson.JSONObject;
import com.apifest.oauth20.unit.OAuthService;
import com.apifest.oauth20.utils.ResponseBuilder;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.StringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

import java.util.Map;

/**
 * Created by Dube on 2018/5/14.
 */
public class UpdateClientApplicationUnit implements Unit {

    @Override
    public Input getInput() {
        return new Input()
                .add("scope", String.class, "支持由空格分割的多个scope", REQUIRED)
                .add("client_id", String.class, "client_id", REQUIRED)
                .add("description", String.class, "用户自定义application描述", REQUIRED)
                .add("application_details", Map.class, "用户自定义的多个键值对", NOT_REQUIRED)
                .add("status", Integer.class, "值为1或者0,1为有效，0为无效", REQUIRED);
    }

    @Override
    public String getName() {
        return "updateClientApplication";
    }

    @Override
    public Group getGroup() {
        return OAuthService.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("更新 client application")
                .setDocApi(true)
                .setSecure(false)
                .setSuccessfulUnitResponse(UnitResponse.createSuccess(new JSONObject() {{
                    put("status", "更新 scope 执行反馈");
                }}));
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) throws Exception {
        JSONObject json = new JSONObject() {{
            put("scope", msg.getString("scope"));
            put("client_id", msg.getString("client_id"));
            put("description", msg.getString("description"));
            put("status", msg.getString("status"));
            if (null != msg.get("application_details")) {
                put("application_details", msg.get("application_details", Map.class));
            }
        }};
        String body = json.toJSONString(), uri = msg.getContext().getUri();

        ByteBuf byteBuffer = Unpooled.wrappedBuffer(body.getBytes());
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri, byteBuffer);
        request.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);

        if (OAuthService.auth.blockingUpdateClientApp(request, msg.getString("client_id"))) {
            handler.handle(UnitResponse.createSuccess(ResponseBuilder.CLIENT_APP_UPDATED));
        }

    }
}
