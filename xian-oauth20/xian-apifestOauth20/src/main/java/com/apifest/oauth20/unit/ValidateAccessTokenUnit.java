package com.apifest.oauth20.unit;

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
        return UnitMeta.createWithDescription("验证accessToken正确性")
                .setDocApi(true)
                .setSecure(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("accessToken", String.class, "token字符串", REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        OAuthService.auth
                .isValidToken(msg.get("accessToken", String.class))
                .subscribe(
                        token -> handler.handle(UnitResponse.createSuccess(token)),
                        exception -> handler.handle(UnitResponse.createException(exception)),
                        () -> handler.handle(UnitResponse.createUnknownError(null, "token required.")))
        ;
    }

    @Override
    public Group getGroup() {
        return OAuthService.singleton;
    }
}
