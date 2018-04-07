package com.apifest.oauth20.unit;

import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.authen.AccessToken;

/**
 * @author happyyangyuan
 */
public class ValidateAccessTokenUnit implements Unit {
    @Override
    public String getName() {
        return "validateAccessToken";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("验证accessToken正确性")
                .setPublic(true)
                .setSecure(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("accessToken", String.class, "token字符串", REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        AccessToken token = OAuthService.auth.isValidToken(msg.get("accessToken", String.class));
        if (token != null) {
            handler.handle(UnitResponse.createSuccess(token));
        } else {
            handler.handle(UnitResponse.createUnknownError(null, "token required."));
        }
    }

    @Override
    public Group getGroup() {
        return OAuthService.singleton;
    }
}
