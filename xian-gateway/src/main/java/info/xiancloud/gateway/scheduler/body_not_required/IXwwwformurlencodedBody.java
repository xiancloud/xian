package info.xiancloud.gateway.scheduler.body_not_required;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.util.HttpUtil;

/**
 * x-www-form-urlencoded form body parser
 *
 * @author happyyangyuan
 */
public interface IXwwwformurlencodedBody extends IBodyParser {
    @Override
    default JSONObject parseBody(String body) throws ReqBodyParseFailure {
        return HttpUtil.parseQueryString(body, false);
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
