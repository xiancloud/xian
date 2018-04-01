package info.xiancloud.httpclient.apache_http.no_auth;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.socket.ISocketGroup;
import info.xiancloud.core.util.RetryUtil;
import info.xiancloud.httpclient.HttpClientGroup;
import info.xiancloud.httpclient.apache_http.IApacheHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;

/**
 * @author happyyangyuan
 */
public class ApacheHttpClientGetUnit implements Unit {
    @Override
    public String getName() {
        return "apacheHttpClientGet";
    }

    @Override
    public Group getGroup() {
        return HttpClientGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("http get请求");
    }


    @Override
    public Input getInput() {
        return new Input()
                .add("url", String.class, "必须是完整的http url", REQUIRED)
                .add("headers", Map.class, "header键值对，必须是字符串键值对")
                .add("readTimeoutInMillis", Integer.class, "响应超时时间，如果不给定那么有一个默认超时时间")
                ;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String url = msg.get("url", String.class);
        Map<String, String> headers = msg.get("headers");
        Integer readTimeoutInMillis = msg.get("readTimeoutInMillis", Integer.class);
        try (IApacheHttpClient httpClient = ApacheHttpClient.newInstance(url, headers, readTimeoutInMillis)) {

            JSONObject responseJSON = new JSONObject();
            HttpResponse httpResponse;
            try {
                httpResponse = RetryUtil.retryUntilNoException(httpClient::getHttpResponse,
                        XianConfig.getIntValue("apache.httpclient.max.try", 3),
                        ConnectTimeoutException.class);
            } catch (ConnectTimeoutException e) {
                return UnitResponse.createError(ISocketGroup.CODE_CONNECT_TIMEOUT, e, "Connect timeout: " + url);
            } catch (SocketTimeoutException e) {
                return UnitResponse.createError(ISocketGroup.CODE_SOCKET_TIMEOUT, e, "Read timeout: " + url);
            } catch (Throwable e) {
                return UnitResponse.createException(e);
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
            return UnitResponse.createSuccess(responseJSON);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
