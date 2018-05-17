package com.apifest.oauth20.unit.scope;

import com.alibaba.fastjson.JSONObject;
import com.apifest.oauth20.unit.OAuthService;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.reactivex.Single;

/**
 * Created by Dube on 2018/5/14.
 */
public class GetScopeUnit implements Unit {

    @Override
    public Input getInput() {
        return new Input().add("scope", String.class, "scope name", REQUIRED);
    }

    @Override
    public String getName() {
        return "getScope";
    }

    @Override
    public Group getGroup() {
        return OAuthService.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("获取单个scop")
                .setDocApi(true)
                .setSecure(false)
                .setSuccessfulUnitResponse(UnitResponse.createSuccess(new JSONObject() {{
                    put("scope", "scope");
                    put("description", "自定义scope描述");
                    put("cc_expires_in", "grant_type为client_credentials时access_token过期时间");
                    put("pass_expires_in", "grant_type为password时access_token过期时间");
                    put("refresh_expires_in", "grant_type为refresh_token时access_token过期时间，如果不填写，则使用pass_expires_in的值");
                }}));
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) throws Exception {
        Single.just(OAuthService.getScopeService().getScopeByName(msg.getString("scope"))).subscribe(
                message -> handler.handle(UnitResponse.createSuccess(message)),
                exception -> handler.handle(UnitResponse.createException(exception))
        );
    }
}
