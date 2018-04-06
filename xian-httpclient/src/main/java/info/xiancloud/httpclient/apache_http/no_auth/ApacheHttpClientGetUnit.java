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
import io.reactivex.Flowable;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;

/**
 * This is a synchronous http client
 *
 * @author happyyangyuan
 * @deprecated because it is synchronous
 */
public class ApacheHttpClientGetUnit implements Unit<JSONObject> {
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
        return UnitMeta.createWithDescription("http get请求");
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
    public Flowable<UnitResponse> execute(UnitRequest msg) {
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
                return Flowable.just(UnitResponse.createError(ISocketGroup.CODE_CONNECT_TIMEOUT, null, "Connect timeout: " + url).setException(e));
            } catch (SocketTimeoutException e) {
                return Flowable.just(UnitResponse.createError(ISocketGroup.CODE_SOCKET_TIMEOUT, null, "Read timeout: " + url).setException(e));
            } catch (Throwable e) {
                return Flowable.just(UnitResponse.createException(e));
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
            return Flowable.just(UnitResponse.createSuccess(responseJSON));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
