package info.xiancloud.gateway.scheduler.body_not_required;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * json body parser
 *
 * @author happyyangyuan
 */
public interface IJsonBody extends IBodyParser {
    default JSONObject parseBody(String httpBody) throws ReqBodyParseFailure {
        try {
            return JSON.parseObject(httpBody);
        } catch (JSONException parseFailed) {
            throw new ReqBodyParseFailure("http request body is not a standard json object string, please check: " + httpBody);
        }
    }
}
