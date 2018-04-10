package info.xiancloud.httpclient.apache_http.basic_auth;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.socket.ISocketGroup;
import info.xiancloud.httpclient.HttpClientGroup;
import info.xiancloud.httpclient.apache_http.IApacheHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;

/**
 * 该unit的返回格式请参见ApacheHttpClientResponseData.json文件
 *
 * @author happyyangyuan
 */
public class BasicAuthApacheHttpClientGetUnit implements Unit {
    @Override
    public String getName() {
        return "basicAuthApacheHttpClientGet";
    }

    @Override
    public Group getGroup() {
        return HttpClientGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("httpBasicAuth get请求")
                .setDocApi(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("url", String.class, "必须是完整的http url", REQUIRED)
                .add("userName", String.class, "httpBasicAuth的用户名", REQUIRED)
                .add("password", String.class, "httpBasicAuth的密码", REQUIRED)
                .add("headers", Map.class, "header键值对，必须是字符串键值对")
                ;
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        String url = msg.get("url", String.class);
        String userName = msg.get("userName", String.class);
        String password = msg.get("password", String.class);
        Map<String, String> headers = msg.get("headers");
        try (IApacheHttpClient httpClient = BasicAuthApacheHttpClient.newInstance(url, userName, password, headers)) {
            JSONObject responseJSON = new JSONObject();
            HttpResponse httpResponse;
            try {
                httpResponse = httpClient.getHttpResponse();
            } catch (ConnectTimeoutException e) {
                handler.handle(UnitResponse.createError(ISocketGroup.CODE_CONNECT_TIMEOUT, null, "Connect timeout: " + url));
                return;
            } catch (SocketTimeoutException e) {
                handler.handle(UnitResponse.createError(ISocketGroup.CODE_SOCKET_TIMEOUT, null, "Read timeout: " + url));
                return;
            } catch (Throwable e) {
                handler.handle(UnitResponse.createException(e));
                return;
            }
            responseJSON.put("statusLine", new JSONObject() {{
                put("statusCode", httpResponse.getStatusLine().getStatusCode());
                put("protocolVersion", httpResponse.getStatusLine().getProtocolVersion());
                put("reasonPhrase", httpResponse.getStatusLine().getReasonPhrase());
            }});
            responseJSON.put("allHeaders", httpResponse.getAllHeaders());
            try {
                responseJSON.put("entity", EntityUtils.toString(httpResponse.getEntity(), "UTF-8"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            handler.handle(UnitResponse.createSuccess(responseJSON));
            return;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
