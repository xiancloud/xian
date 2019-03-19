package info.xiancloud.apifestoauth20.unit.scope;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.apifestoauth20.unit.OAuthService;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import io.reactivex.Single;

/**
 * Created by Dube on 2018/5/14.
 */
public class DeleteScopeUnit implements Unit {

    @Override
    public Input getInput() {
        return new Input().add("scope", String.class, "scope name", REQUIRED);
    }

    @Override
    public String getName() {
        return "deleteScope";
    }

    @Override
    public Group getGroup() {
        return OAuthService.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("删除 scope ")
                .setDocApi(true)
                .setSecure(false)
                .setSuccessfulUnitResponse(UnitResponse.createSuccess(new JSONObject() {{
                    put("status", "删除 scope 执行反馈");
                }}));
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) throws Exception {
        Single.just(OAuthService.getScopeService().deleteScope(msg.getString("scope"))).subscribe(
                message -> handler.handle(UnitResponse.createSuccess(message)),
                exception -> handler.handle(UnitResponse.createException(exception))
        );
    }
}
