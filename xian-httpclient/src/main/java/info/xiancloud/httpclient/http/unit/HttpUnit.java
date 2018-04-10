package info.xiancloud.httpclient.http.unit;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.socket.ISocketGroup;
import info.xiancloud.core.util.http.Request;
import info.xiancloud.core.util.http.Response;
import info.xiancloud.httpclient.HttpClientGroup;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Base64;

/**
 * @author happyyangyuan
 * @deprecated not fully tested.
 */
public class HttpUnit implements Unit {
    @Override
    public Group getGroup() {
        return HttpClientGroup.singleton;
    }

    @Override
    public String getName() {
        return "http";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("http请求")
                .setDocApi(false);
    }

    @Override
    public Input getInput() {
        return new Input().add("req", String.class, "请求对象序列化后的 Base64编码字符串", REQUIRED);

    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        ObjectInputStream ois = null;
        try {
            String reqBase64 = msg.get("req", String.class);
            ois = new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(reqBase64)));
            Request request = (Request) ois.readObject();
            Response response;
            try {
                response = request.executeLocal();
            } catch (ConnectException e) {
                handler.handle(UnitResponse.createError(ISocketGroup.CODE_CONNECT_TIMEOUT, null, "Connect timeout: " + request.getUrl()));
                return;
            } catch (SocketTimeoutException e) {
                handler.handle(UnitResponse.createError(ISocketGroup.CODE_SOCKET_TIMEOUT, null, "Read timeout: " + request.getUrl()));
                return;
            } catch (Throwable e) {
                handler.handle(UnitResponse.createException(e));
                return;
            }
            JSONObject retJson = new JSONObject();
            retJson.put("status", response.getStatus());
            retJson.put("headers", response.getHeaders());
            retJson.put("entity", response.string());
            handler.handle(UnitResponse.createSuccess(retJson));
        } catch (Throwable e) {
            handler.handle(UnitResponse.createException(e));
        }
    }

}
