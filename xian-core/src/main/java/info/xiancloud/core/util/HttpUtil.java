package info.xiancloud.core.util;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Constant;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.socket.ConnectTimeoutException;
import info.xiancloud.core.socket.ISocketGroup;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.reactivex.Single;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;

/**
 * HttpUtil class
 *
 * @author jeff, happyyangyuan
 */
public class HttpUtil {

    //default to UTF-8
    private static String ENCODING = Constant.DEFAULT_ENCODING;

    /**
     * @param map the parameter map
     * @return the x-www-form-urlencoded string
     */
    public static String xwwwformEncode(Map map) {
        /*
        The following code is based on apache http client, which we have removed it from xian-core dependencies.

        String applicationXwwwformUrlEncodedString;
        if (paramsMap != null) {
            List<NameValuePair> paramList = new ArrayList<>();
            for (Object key : paramsMap.keySet()) {
                if (key != null && paramsMap.get(key) != null) {
                    NameValuePair pair = new BasicNameValuePair(key.toString(), paramsMap.get(key).toString());
                    paramList.add(pair);
                }
            }
            applicationXwwwformUrlEncodedString = URLEncodedUtils.format(paramList, ENCODING);
        } else {
            applicationXwwwformUrlEncodedString = "";
            LOG.warn("post请求的body参数为空，请确认");
        }
        return applicationXwwwformUrlEncodedString;
        */
        StringBuilder stringBuilder = new StringBuilder();
        if (map == null) {
            return stringBuilder.toString();
        } else {
            for (Object entryObject : map.entrySet()) {
                Map.Entry entry = (Map.Entry) entryObject;
                stringBuilder.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue())
                        .append("&");
            }
            return stringBuilder.substring(0, stringBuilder.length() - 1);
        }
    }

    /**
     * parse the given http query string
     *
     * @param queryString the standard http query string
     * @param hasPath     whether the query string contains uri
     * @return the parsed json object. if the given query string is empty then an empty json object is returned.
     */
    public static JSONObject parseQueryString(String queryString, boolean hasPath) {
        JSONObject uriParameters = new JSONObject();
        if (queryString == null) return uriParameters;
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(queryString, hasPath);
        Map<String, List<String>> parameters = queryStringDecoder.parameters();
        parameters.forEach((key, values) -> {
            if (values == null || values.isEmpty()) {
                LOG.debug("空参数统一对应空字符串");
                uriParameters.put(key, "");
            } else if (values.size() == 1)
                uriParameters.put(key, values.get(0));
            else
                uriParameters.put(key, values);
        });
        return uriParameters;
    }

    public static Single<String> post(String url, String body, Map<String, String> headers) {
        return SingleRxXian
                .call("httpClient", "apacheHttpClientPost",
                        new JSONObject()
                                .fluentPut("url", url)
                                .fluentPut("body", body)
                                .fluentPut("headers", headers))
                .flatMap(response -> {
                    if (response.succeeded()) return Single.just(response.dataToJson().getString("entity"));
                    else return Single.error(ioException(response, url));
                });
    }

    public static Single<String> get(String url, Map<String, String> headers) {
        return SingleRxXian
                .call("httpClient", "apacheHttpClientGet", new JSONObject() {{
                    put("url", url);
                    put("headers", headers);
                }})
                .flatMap(response -> {
                    if (response.succeeded()) return Single.just(response.dataToJson().getString("entity"));
                    else return Single.error(ioException(response, url));
                });
    }

    private static Throwable ioException(UnitResponse unitResponse, String url) {
        switch (unitResponse.getCode()) {
            case ISocketGroup.CODE_CONNECT_TIMEOUT:
                return new ConnectTimeoutException("Connection timeout: " + url);
            case ISocketGroup.CODE_SOCKET_TIMEOUT:
                return new SocketTimeoutException("read timed out: " + url);
            default:
                return new RuntimeException(String.format("request for url=%s failed, output=%s", url, unitResponse));
        }
    }

    public static Single<String> postWithEmptyHeader(String url, String body) {
        return post(url, body, null);
    }

    public static Single<String> get(String url) {
        return get(url, null);
    }

    public static String delete(String url, Map<String, String> headers) {
        // TODO
        return null;
    }

    public static String delete(String url, String body, Map<String, String> headers) {
        // TODO
        return null;
    }

    public static String put(String url, Map<String, String> headers) {
        // TODO
        return null;
    }

    public static String put(String url, String body, Map<String, String> headers) {
        // TODO
        return null;
    }

}