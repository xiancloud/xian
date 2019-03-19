package info.xiancloud.apifestoauth20.unit.token;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.apifest.oauth20.persistence.DBManagerFactory;
import info.xiancloud.apifestoauth20.unit.OAuthService;
import com.apifest.oauth20.utils.QueryParameter;
import com.apifest.oauth20.utils.ResponseBuilder;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.authen.AccessToken;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

/**
 * Created by Dube on 2018/5/14.
 */
public class GetAllActiveAccessTokensUnit implements Unit {

    @Override
    public Input getInput() {
        return new Input()
                .add("client_id", String.class, "client_id", REQUIRED)
                .add("user_id", String.class, "用户获取code时自定义的user_id", REQUIRED);
    }

    @Override
    public String getName() {
        return "getAllActiveAccessTokensUnit";
    }

    @Override
    public Group getGroup() {
        return OAuthService.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("查询有效的 token")
                .setDocApi(true)
                .setSecure(false)
                .setSuccessfulUnitResponse(UnitResponse.createSuccess(new JSONObject() {{
                    put("valid", true);
                    put("access_token", "access_token");
                    put("refresh_token", "refresh_token");
                    put("user_id", "user_id");
                    put("created", "token创建时间，时间戳，单位为ms");
                    put("refresh_expires_in", "refresh_expires_in");
                    put("scope", "scope");
                    put("token_type", "token_type");
                    put("expires_in", "过期时间，数字类型，单位为s");
                    put("client_id", "client_id");
                }}));
    }

    @Override
    public void execute(UnitRequest request, Handler<UnitResponse> handler) throws Exception {
        String clientId = request.getString("client_id");
        String userId = request.getString("user_id");
        if (clientId == null || clientId.isEmpty()) {
            handler.handle(UnitResponse.createSuccess(String.format(ResponseBuilder.MANDATORY_PARAM_MISSING, QueryParameter.CLIENT_ID)));
        } else if (userId == null || userId.isEmpty()) {
            handler.handle(UnitResponse.createSuccess(String.format(ResponseBuilder.MANDATORY_PARAM_MISSING, QueryParameter.USER_ID)));
        } else {
            // check that LOCAL_NODE_ID exists, no matter whether it is active or not
            if (!OAuthService.auth.isExistingClient(clientId).blockingGet()) {
                handler.handle(UnitResponse.createSuccess(ResponseBuilder.INVALID_CLIENT_ID));
            } else {
                AccessToken accessTokens = DBManagerFactory.getInstance().getAccessTokenByUserIdAndClientId(userId, clientId).blockingGet();
                handler.handle(UnitResponse.createSuccess(accessTokens));
            }
        }
    }
}
