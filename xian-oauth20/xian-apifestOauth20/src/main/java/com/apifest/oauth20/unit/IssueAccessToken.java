package com.apifest.oauth20.unit;

import com.alibaba.fastjson.JSONObject;
import com.apifest.oauth20.bean.OAuthException;
import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.support.authen.AccessToken;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

/**
 * Issue a new access token.
 *
 * @author happyyangyuan
 */
public class IssueAccessToken implements Unit {
    @Override
    public String getName() {
        return "issueAccessToken";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("分配token，token会在指定的时间内过期，过期后需要重新申请。")
                .setPublic(true)
                .setSecure(false)
                .setSuccessfulUnitResponse(UnitResponse.success(new JSONObject() {{
                    put("valid", true);
                    put("expiresIn", "过期时间，数字类型，单位为s");
                    put("created", "token创建时间，时间戳，单位为ms");
                    put("appId", "token所属的appId，与入参appId相同");
                    put("accessToken", "accessToken");
                }}));
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("appId", String.class, "appId", REQUIRED)
                .add("appSecret", String.class, "app密码", REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        JSONObject json = new JSONObject() {{
            put("client_id", msg.getString("appId"));
            put("client_secret", msg.getString("appSecret"));
            put("grant_type", "client_credentials");
        }};
        String body = json.toJSONString(),
                uri = msg.getString("$url");
        ByteBuf byteBuffer = Unpooled.wrappedBuffer(body.getBytes());
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri, byteBuffer);
        try {
            AccessToken token = OAuthService.auth.issueAccessToken(request);
            return UnitResponse.success(new JSONObject() {{
                put("appId", msg.getString("appId"));
                put("accessToken", token.getToken());
                put("valid", token.isValid());
                put("expiresIn", token.getExpiresIn());
                put("created", token.getCreated());
                put("scope", token.getScope());
            }});
        } catch (OAuthException e) {
            return UnitResponse.exception(e);
        }
    }

    @Override
    public Group getGroup() {
        return OAuthService.singleton;
    }

}
