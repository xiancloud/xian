package info.xiancloud.apifestoauth20.unit.token;

import com.alibaba.fastjson.JSONObject;
import com.apifest.oauth20.bean.OAuthException;
import info.xiancloud.apifestoauth20.unit.OAuthService;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.authen.AccessToken;
import info.xiancloud.core.util.LOG;
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
        return UnitMeta.createWithDescription("分配token，token会在指定的时间内过期，过期后需要重新申请。")
                .setDocApi(true)
                .setSecure(false)
                .setSuccessfulUnitResponse(UnitResponse.createSuccess(new JSONObject() {{
                    put("valid", true);
                    put("expires_in", "过期时间，数字类型，单位为s");
                    put("created", "token创建时间，时间戳，单位为ms");
                    put("client_id", "token所属的appId，与入参appId相同");
                    put("access_token", "access_token");
                    put("refresh_token", "refresh_token");
                    put("refresh_expires_in", "refresh_expires_in");
                    put("scope", "scope");
                    put("token_type", "token type");
                    put("user_id", "user id");
                }}));
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("grant_type", String.class, "grant_type有四种类型，分别为authorization_code，refresh_token，client_credentials，password", REQUIRED)
                .add("client_id", String.class, "client_id", REQUIRED)
                .add("client_secret", String.class, "client_secret", REQUIRED)
                .add("redirect_uri", String.class, "仅当grant_type为authorization_code时必填", NOT_REQUIRED)
                .add("code", String.class, "仅当grant_type为authorization_code时必填", NOT_REQUIRED)
                .add("refresh_token", String.class, "仅当grant_type为refresh_token时必填", NOT_REQUIRED)
                .add("scope", String.class, "仅当grant_type为refresh_token,client_credentials时填写有效", NOT_REQUIRED)
                .add("username", String.class, "仅当grant_type为password时必填", NOT_REQUIRED)
                .add("password", String.class, "仅当grant_type为password时必填", NOT_REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        JSONObject json = new JSONObject() {{
            put("grant_type", msg.getString("grant_type"));
            put("client_id", msg.getString("client_id"));
            put("client_secret", msg.getString("client_secret"));
            if (null != msg.get("redirect_uri")) put("redirect_uri", msg.getString("redirect_uri"));
            if (null != msg.get("code")) put("code", msg.getString("code"));
            if (null != msg.get("refresh_token")) put("refresh_token", msg.getString("refresh_token"));
            if (null != msg.get("scope")) put("scope", msg.getString("scope"));
            if (null != msg.get("username")) put("username", msg.getString("username"));
            if (null != msg.get("password")) put("password", msg.getString("password"));
        }};
        String body = json.toJSONString(),
                uri = msg.getContext().getUri();
        ByteBuf byteBuffer = Unpooled.wrappedBuffer(body.getBytes());
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri, byteBuffer);
        try {
            AccessToken token = OAuthService.auth.blockingIssueAccessToken(request);
            handler.handle(UnitResponse.createSuccess(token));
        } catch (OAuthException e) {
            LOG.error(e);
            handler.handle(UnitResponse.createException(e));
        }
    }

    @Override
    public Group getGroup() {
        return OAuthService.singleton;
    }

}
