package info.xiancloud.plugin.httpclient.apache_http.basic_auth;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.*;
import info.xiancloud.plugin.httpclient.HttpClientGroup;
import info.xiancloud.plugin.httpclient.apache_http.IApacheHttpClient;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.socket.ISocketGroup;
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
        return UnitMeta.create("httpBasicAuth get请求");
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
    public UnitResponse execute(UnitRequest msg) {
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
                return UnitResponse.error(ISocketGroup.CODE_CONNECT_TIMEOUT, null, "Connect timeout: " + url);
            } catch (SocketTimeoutException e) {
                return UnitResponse.error(ISocketGroup.CODE_SOCKET_TIMEOUT, null, "Read timeout: " + url);
            } catch (Throwable e) {
                return UnitResponse.exception(e);
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
            return UnitResponse.success(responseJSON);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
