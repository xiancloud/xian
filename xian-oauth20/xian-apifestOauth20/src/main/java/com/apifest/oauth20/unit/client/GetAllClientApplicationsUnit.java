package com.apifest.oauth20.unit.client;

import com.alibaba.fastjson.JSONObject;
import com.apifest.oauth20.persistence.DBManagerFactory;
import com.apifest.oauth20.unit.OAuthService;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;

import java.util.stream.Collectors;

/**
 * Created by Dube on 2018/5/14.
 */
public class GetAllClientApplicationsUnit implements Unit {

    @Override
    public Input getInput() {
        return new Input()
                .add("status", Integer.class, "筛选条件，非必填，若填写则获取对应status的application", NOT_REQUIRED);
    }

    @Override
    public String getName() {
        return "getAllClientApplications";
    }

    @Override
    public Group getGroup() {
        return OAuthService.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("获取所有 client application，可设置 status 条件")
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
        if (request.getArgMap().containsKey("status")) {
            Integer status = request.get("status", Integer.class);
            handler.handle(
                    UnitResponse.createSuccess(
                            DBManagerFactory.getInstance().getAllApplications().blockingGet()
                                    .stream().filter(appInfo -> appInfo.getStatus().equals(status))
                                    .collect(Collectors.toList())));
        } else {
            handler.handle(
                    UnitResponse.createSuccess(
                            DBManagerFactory.getInstance().getAllApplications().blockingGet()));
        }

    }
}
