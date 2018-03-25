package info.xiancloud.gateway.scheduler.body_not_required;

import com.alibaba.fastjson.JSONObject;

/**
 * http body parser interface
 *
 * @author happyyangyuan
 */
public interface IBodyParser {
    JSONObject parseBody(String body) throws ReqBodyParseFailure;
}
