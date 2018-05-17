package com.apifest.oauth20.unit;

import com.alibaba.fastjson.JSONObject;
import com.apifest.oauth20.bean.OAuthException;
import com.apifest.oauth20.utils.ResponseBuilder;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

import java.util.Map;

/**
 * Created by Dube on 2018/5/14.
 */
public class IssueAuthorizationCodeUnit implements Unit {

    @Override
    public Input getInput() {
        return new Input()
                .add("response_type", String.class, "response_type仅支持code类型", REQUIRED)
                .add("client_id", String.class, "client_id", REQUIRED)
                .add("state", String.class, "state为用户自定义内容，重定向时会带上该参数", NOT_REQUIRED)
                .add("redirect_uri", String.class, "redirect_uri", REQUIRED)
                .add("user_id", String.class, "用户自定义值", NOT_REQUIRED)
                .add("scope", String.class, "支持由空格分割的多个scope", REQUIRED);
    }

    @Override
    public String getName() {
        return "issueAuthorizationCode";
    }

    @Override
    public Group getGroup() {
        return OAuthService.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("分配 Authorization Code")
                .setDocApi(true)
                .setSecure(false);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) throws Exception {
        StringBuffer uriBuffer = new StringBuffer(msg.getContext().getUri())
                .append("?").append("response_type=").append(msg.getString("response_type"))
                .append("&").append("client_id=").append(msg.getString("client_id"))
                .append("&").append("redirect_uri=").append(msg.getString("redirect_uri"))
                .append("&").append("scope=").append(msg.getString("scope"));
        if (null != msg.get("state")) {
            uriBuffer.append("&").append("state=").append(msg.getString("state"));
        }
        if (null != msg.get("user_id")) {
            uriBuffer.append("&").append("user_id=").append(msg.getString("user_id"));
        }
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uriBuffer.toString());
        request.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);

        try {
            String redirectURI = OAuthService.auth.blockingIssueAuthorizationCode(request);
            LOG.info(String.format("redirectURI: %s", redirectURI));
            handler.handle(UnitResponse.createSuccess(new JSONObject() {{
                put("redirect_uri", redirectURI);
            }}));
        } catch (OAuthException ex) {
            handler.handle(UnitResponse.createException(ex));
        }
    }
}
