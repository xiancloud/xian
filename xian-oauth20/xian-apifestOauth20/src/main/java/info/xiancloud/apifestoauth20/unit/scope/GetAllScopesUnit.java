package info.xiancloud.apifestoauth20.unit.scope;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.apifestoauth20.unit.OAuthService;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.reactivex.Single;

/**
 * Created by Dube on 2018/5/14.
 */
public class GetAllScopesUnit implements Unit {

    @Override
    public Input getInput() {
        return new Input()
                .add("client_id", String.class, "筛选条件：client_id", NOT_REQUIRED);
    }

    @Override
    public String getName() {
        return "getAllScopes";
    }

    @Override
    public Group getGroup() {
        return OAuthService.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("获取所有 scope")
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
        StringBuffer uriBuffer = new StringBuffer(msg.getContext().getUri());
        if (null != msg.getString("client_id")) {
            uriBuffer.append("?client_id=").append(msg.getString("client_id"));
        }
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uriBuffer.toString());
        Single.just(OAuthService.getScopeService().getScopes(request)).subscribe(
                message -> handler.handle(UnitResponse.createSuccess(message)),
                exception -> handler.handle(UnitResponse.createException(exception))
        );
    }
}
