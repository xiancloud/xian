package info.xiancloud.plugin.httpclient.http.unit;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.*;
import info.xiancloud.plugin.httpclient.HttpClientGroup;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.socket.ISocketGroup;
import info.xiancloud.plugin.util.http.Response;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Base64;

/**
 * @author happyyangyuan
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
        return UnitMeta.create("http请求");
    }

    @Override
    public Input getInput() {
        return new Input().add("req", String.class, "请求对象序列化后的 Base64编码字符串", REQUIRED);

    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        ObjectInputStream ois = null;
        try {
            String reqBase64 = msg.get("req", String.class);
            ois = new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(reqBase64)));
            info.xiancloud.plugin.util.http.Request request = (info.xiancloud.plugin.util.http.Request) ois.readObject();
            Response response = null;
            try {
                response = request.executeLocal();
            } catch (ConnectException e) {
                return UnitResponse.error(ISocketGroup.CODE_CONNECT_TIMEOUT, null, "Connect timeout: " + request.getUrl());
            } catch (SocketTimeoutException e) {
                return UnitResponse.error(ISocketGroup.CODE_SOCKET_TIMEOUT, null, "Read timeout: " + request.getUrl());
            } catch (Throwable e) {
                return UnitResponse.exception(e);
            }
            JSONObject retJson = new JSONObject();
            retJson.put("status", response.getStatus());
            retJson.put("headers", response.getHeaders());
            retJson.put("entity", response.string());
            return UnitResponse.success(retJson);
        } catch (Throwable e) {
            return UnitResponse.exception(e);
        } finally {

        }
    }

}
