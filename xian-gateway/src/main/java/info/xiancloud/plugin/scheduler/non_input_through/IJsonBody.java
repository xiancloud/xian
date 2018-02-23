package info.xiancloud.plugin.scheduler.non_input_through;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * json body parser
 *
 * @author happyyangyuan
 */
public interface IJsonBody extends IBodyParser {
    default JSONObject parseBody(String $body) throws ReqBodyParseFailure {
        try {
            return JSON.parseObject($body);
        } catch (JSONException parseFailed) {
            throw new ReqBodyParseFailure("http请求内容不符合规范，不是json格式:" + $body);
        }
    }
}
