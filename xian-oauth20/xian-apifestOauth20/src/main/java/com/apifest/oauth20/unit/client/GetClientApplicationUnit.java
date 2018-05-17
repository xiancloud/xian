package com.apifest.oauth20.unit.client;

import com.alibaba.fastjson.JSONObject;
import com.apifest.oauth20.unit.OAuthService;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;

/**
 * Created by Dube on 2018/5/14.
 */
public class GetClientApplicationUnit implements Unit {

    @Override
    public Input getInput() {
        return new Input()
                .add("client_id", String.class, "client application id", REQUIRED);
    }

    @Override
    public String getName() {
        return "getClientApplication";
    }

    @Override
    public Group getGroup() {
        return OAuthService.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("根据 id 获取 client application")
                .setDocApi(true)
                .setSecure(false)
                .setSuccessfulUnitResponse(UnitResponse.createSuccess(new JSONObject() {{
                    put("client_id", "client id");
                    put("client_secret", "client 密钥");
                    put("scope", "client application名称");
                    put("name", "token所属的appId，与入参appId相同");
                    put("registered", "注册时间");
                    put("redirect_uri", "redirect_uri");
                    put("description", "application描述");
                    put("status", "值为1或者0,1为有效，0为无效");
                }}));
    }

    @Override
    public void execute(UnitRequest request, Handler<UnitResponse> handler) throws Exception {
        OAuthService.auth.getApplicationInfo(request.getString("client_id")).subscribe(
                info -> handler.handle(UnitResponse.createSuccess(info)),
                exception -> handler.handle(UnitResponse.createException(exception)),
                () -> handler.handle(UnitResponse.createSuccess(new JSONObject() {{
                    put("message", "client application don't exist");
                }}))
        );
    }
}
