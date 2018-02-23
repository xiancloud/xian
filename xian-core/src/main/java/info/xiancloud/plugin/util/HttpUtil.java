package info.xiancloud.plugin.util;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.Constant;
import info.xiancloud.plugin.message.SyncXian;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.socket.ConnectTimeoutException;
import info.xiancloud.plugin.socket.ISocketGroup;

import java.net.SocketTimeoutException;
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

    public static String post(String url, String body, Map<String, String> headers) throws ConnectTimeoutException, SocketTimeoutException {
        UnitResponse out = SyncXian.call("httpClient", "apacheHttpClientPost", new JSONObject() {{
            put("url", url);
            put("body", body);
            put("headers", headers);
        }});
        if (out.succeeded()) {
            return out.dataToJson().getString("entity");
        } else {
            throwIOException(out, url);
            //dead code below
            return out.toJSONString();
        }
    }

    public static String get(String url, Map<String, String> headers) throws ConnectTimeoutException, SocketTimeoutException {
        UnitResponse out = SyncXian.call("httpClient", "apacheHttpClientGet", new JSONObject() {{
            put("url", url);
            put("headers", headers);
        }});
        if (out.succeeded()) {
            return out.dataToJson().getString("entity");
        } else {
            throwIOException(out, url);
            return out.toJSONString();//dead code below
        }
    }

    private static void throwIOException(UnitResponse out, String url) throws ConnectTimeoutException, SocketTimeoutException {
        switch (out.getCode()) {
            case ISocketGroup.CODE_CONNECT_TIMEOUT:
                throw new ConnectTimeoutException("Connection timeout: " + url);
            case ISocketGroup.CODE_SOCKET_TIMEOUT:
                throw new SocketTimeoutException("read timed out: " + url);
            default:
                throw new RuntimeException(String.format("request for url=%s failed, output=%s", url, out));
        }
    }

    public static String postWithEmptyHeader(String url, String body) throws SocketTimeoutException, ConnectTimeoutException {
        return post(url, body, null);
    }

    public static String httpGet(String url) throws SocketTimeoutException, ConnectTimeoutException {
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