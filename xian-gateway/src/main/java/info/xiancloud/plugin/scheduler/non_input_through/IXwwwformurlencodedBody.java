package info.xiancloud.plugin.scheduler.non_input_through;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.util.LOG;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

/**
 * x-www-form-urlencoded form body parser
 *
 * @author happyyangyuan
 */
public interface IXwwwformurlencodedBody extends IBodyParser {
    @Override
    default JSONObject parseBody(String body) throws ReqBodyParseFailure {
        return parseByNettyDecoder(body);
    }

    /**
     * parse the http request body using the netty query string decoder.
     */
    static JSONObject parseByNettyDecoder(String body) {
        JSONObject bodyParameters = new JSONObject();
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(body, false);
        Map<String, List<String>> parameters = queryStringDecoder.parameters();
        parameters.forEach((key, values) -> {
            if (values == null || values.isEmpty()) {
                LOG.debug("空参数统一对应空字符串");
                bodyParameters.put(key, "");
            } else if (values.size() == 1)
                bodyParameters.put(key, values.get(0));
            else
                bodyParameters.put(key, values);
        });
        return bodyParameters;
    }

    /**
     * simply parse the http request body.
     *
     * @deprecated the short coming is that array parameters are not passed correctly.
     */
    static JSONObject sampleParser(String body) {
        JSONObject originMap = new JSONObject();
        String[] keyValues = body.split("&");
        for (String keyValue : keyValues) {
            String[] keyValueArray = keyValue.split("=");
            originMap.put(keyValueArray[0], keyValueArray[1]);
        }
        return originMap;
    }
}
