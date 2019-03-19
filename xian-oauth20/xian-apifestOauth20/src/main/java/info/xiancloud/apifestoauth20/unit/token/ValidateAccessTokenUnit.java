package info.xiancloud.apifestoauth20.unit.token;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.apifestoauth20.unit.OAuthService;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;

/**
 * validateAccessToken
 *
 * @author happyyangyuan
 */
public class ValidateAccessTokenUnit implements Unit {
    @Override
    public String getName() {
        return "validateAccessToken";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("验证access_token正确性")
                .setDocApi(true)
                .setSecure(false)
                .setSuccessfulUnitResponse(UnitResponse.createSuccess(new JSONObject() {{
                    put("valid", true);
                    put("access_token", "access_token");
                    put("refresh_token", "refresh_token");
                    put("user_id", "user id");
                    put("created", "创建时间戳");
                    put("refresh_expires_in", "refresh_expires_in");
                    put("scope", "scope");
                    put("token_type", "token_type");
                    put("expires_in", "expires_in");
                    put("client_id", "client_id");
                }}));
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("access_token", String.class, "token字符串", REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        OAuthService.auth
                .isValidToken(msg.get("access_token", String.class))
                .subscribe(
                        token -> handler.handle(UnitResponse.createSuccess(token)),
                        exception -> handler.handle(UnitResponse.createException(exception)),
                        () -> handler.handle(UnitResponse.createUnknownError(null, "token required.")));
    }

    @Override
    public Group getGroup() {
        return OAuthService.singleton;
    }
}
