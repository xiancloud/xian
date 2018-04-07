package info.xiancloud.httpclient.apache_http.no_auth;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.*;
import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.socket.ISocketGroup;
import info.xiancloud.core.util.RetryUtil;
import info.xiancloud.httpclient.HttpClientGroup;
import info.xiancloud.httpclient.apache_http.IApacheHttpClient;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;

/**
 * @author happyyangyuan
 */
public class ApacheHttpClientPostUnit implements Unit {
    @Override
    public String getName() {
        return "apacheHttpClientPost";
    }

    @Override
    public Group getGroup() {
        return HttpClientGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("http post请求");
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("url", String.class, "必须是完整的http url", REQUIRED)
                .add("headers", Map.class, "header键值对，必须是字符串键值对")
                .add("body", String.class, "post请求的body")
                .add("readTimeoutInMillis", Integer.class, "响应超时时间，如果不给定那么有一个默认超时时间")
                ;
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        String url = msg.get("url", String.class);
        Map<String, String> headers = msg.get("headers");
        Integer readTimeoutInMillis = msg.get("readTimeoutInMillis", Integer.class);
        try (IApacheHttpClient httpClient = ApacheHttpClient.newInstance(url, headers, readTimeoutInMillis)) {
            JSONObject responseJSON = new JSONObject();
            String responsePayload;
            try {
                responsePayload = RetryUtil.retryUntilNoException(
                        () -> httpClient.post(msg.get("body", String.class)),
                        XianConfig.getIntValue("apache.httpclient.max.try", 3),
                        ConnectTimeoutException.class);//这里对连接超时做重试，总共只尝试三次
            } catch (ConnectTimeoutException e) {
                handler.handle(UnitResponse.createError(ISocketGroup.CODE_CONNECT_TIMEOUT, e, "Connect timeout: " + url));
                return;
            } catch (SocketTimeoutException e) {
                handler.handle(UnitResponse.createError(ISocketGroup.CODE_SOCKET_TIMEOUT, e, "Read timeout: " + url));
                return;
            } catch (Throwable e) {
                handler.handle(UnitResponse.createException(e));
                return;
            }
            responseJSON.put("statusLine", new JSONObject() {{
                put("statusCode", "todo");
                put("protocolVersion", "todo");
                put("reasonPhrase", "todo");
            }});
            responseJSON.put("allHeaders", "todo");
            responseJSON.put("entity", responsePayload);
            handler.handle(UnitResponse.createSuccess(responseJSON));
            return;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
