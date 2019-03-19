package info.xiancloud.apifestoauth20.unit.token;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.apifestoauth20.unit.OAuthService;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

/**
 * Created by Dube on 2018/5/14.
 */
public class RevokeAccessTokenUnit implements Unit {

    @Override
    public Input getInput() {
        return new Input()
                .add("access_token", String.class, "access_token", REQUIRED)
                .add("client_id", String.class, "client_id", REQUIRED);
    }

    @Override
    public String getName() {
        return "revokeAccessToken";
    }

    @Override
    public Group getGroup() {
        return OAuthService.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("撤销已经获取的access_token")
                .setDocApi(true)
                .setSecure(false);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) throws Exception {
        JSONObject json = new JSONObject() {{
            put("client_id", msg.getString("client_id"));
            put("access_token", msg.getString("access_token"));
        }};
        String body = json.toJSONString(),
                uri = msg.getContext().getUri();
        ByteBuf byteBuffer = Unpooled.wrappedBuffer(body.getBytes());
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri, byteBuffer);
        OAuthService.auth.revokeToken(request).subscribe(
                message -> handler.handle(UnitResponse.createSuccess(message)),
                exception -> handler.handle(UnitResponse.createException(exception))
        );
    }
}
