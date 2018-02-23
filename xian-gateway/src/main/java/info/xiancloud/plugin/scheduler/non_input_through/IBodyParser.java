package info.xiancloud.plugin.scheduler.non_input_through;

import com.alibaba.fastjson.JSONObject;

/**
 * http body parser interface
 *
 * @author happyyangyuan
 */
public interface IBodyParser {
    JSONObject parseBody(String body) throws ReqBodyParseFailure;
}
