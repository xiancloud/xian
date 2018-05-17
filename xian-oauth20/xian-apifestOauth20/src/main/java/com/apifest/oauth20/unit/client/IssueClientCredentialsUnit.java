package com.apifest.oauth20.unit.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.apifest.oauth20.bean.ClientCredentials;
import com.apifest.oauth20.bean.OAuthException;
import com.apifest.oauth20.unit.OAuthService;
import com.apifest.oauth20.utils.ResponseBuilder;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.authen.AccessToken;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

import java.util.Map;

/**
 * Created by Dube on 2018/5/14.
 */
public class IssueClientCredentialsUnit implements Unit {

    @Override
    public Input getInput() {
        return new Input()
                .add("name", String.class, "application名称", REQUIRED)
                .add("scope", String.class, "支持由空格分割的多个scope", REQUIRED)
                .add("redirect_uri", String.class, "redirect_uri", REQUIRED)
                .add("client_id", String.class, "client_id", NOT_REQUIRED)
                .add("client_secret", String.class, "client_secret", NOT_REQUIRED)
                .add("description", String.class, "用户自定义application描述", NOT_REQUIRED)
                .add("application_details", Map.class, "用户自定义的多个键值对", NOT_REQUIRED);
    }

    @Override
    public String getName() {
        return "issueClientCredentials";
    }

    @Override
    public Group getGroup() {
        return OAuthService.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("注册 client application")
                .setDocApi(true)
                .setSecure(false)
                .setSuccessfulUnitResponse(UnitResponse.createSuccess(new JSONObject() {{
                    put("client_id", "client id");
                    put("client_secret", "client 密钥");
                    put("scope", "client application名称");
                    put("name", "token所属的appId，与入参appId相同");
                    put("created", "create 时间戳");
                    put("redirect_uri", "redirect_uri");
                    put("description", "application描述");
                    put("type", "client application 类型；public or confidential"); //TODO which value of type is public ? 1 or 0
                    put("status", "状态：1 可用，0 不可用");
                }}));
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) throws Exception {

        JSONObject json = new JSONObject() {{
            put("name", msg.getString("name"));
            put("scope", msg.getString("scope"));
            put("redirect_uri", msg.getString("redirect_uri"));

            if (!StringUtil.isEmpty(msg.getString("client_id"))) {
                put("client_id", msg.getString("client_id"));
            }
            if (!StringUtil.isEmpty(msg.getString("client_secret"))) {
                put("client_secret", msg.getString("client_secret"));
            }
            if (!StringUtil.isEmpty(msg.getString("description"))) {
                put("description", msg.getString("description"));
            }
            if (null != msg.get("application_details", Map.class)) {
                put("application_details", msg.get("application_details", Map.class));
            }
        }};
        String body = json.toJSONString(), uri = msg.getContext().getUri();

        ByteBuf byteBuffer = Unpooled.wrappedBuffer(body.getBytes());
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri, byteBuffer);
        request.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);


        OAuthService.auth.issueClientCredentials(request).subscribe(
                clientCredentials -> handler.handle(UnitResponse.createSuccess(clientCredentials)),
                exception -> handler.handle(UnitResponse.createException(exception))
        );


    }
}
