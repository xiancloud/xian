package info.xiancloud.plugin.scheduler;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.util.LOG;

import java.util.Map;

/**
 * response data only
 *
 * @author happyyangyuan
 */
public interface IResponseDataOnly extends IResponseExtractor {
    default String extractContext(UnitResponse unitResponse) {
        if (!unitResponse.succeeded()) {
            return unitResponse.toVoJSONString();
        }
        if (unitResponse.getData() != null && unitResponse.getData() instanceof Map) {
            LOG.debug("这里对于需要透传的字段直接透传出去给外部，而不是返回一个unitResponse");
            JSONObject data = unitResponse.dataToJson();
            return data.toJSONString();
        }
        if (unitResponse.getData() != null && !(unitResponse.getData() instanceof Map))
            return unitResponse.dataToType(String.class);
        return "";
    }
}
