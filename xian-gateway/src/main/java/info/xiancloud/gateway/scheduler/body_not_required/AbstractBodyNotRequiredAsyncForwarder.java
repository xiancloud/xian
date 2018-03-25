package info.xiancloud.gateway.scheduler.body_not_required;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Constant;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.util.LOG;
import info.xiancloud.gateway.scheduler.AbstractAsyncForwarder;

import java.util.Iterator;
import java.util.Map;

/**
 * controller scheduler whose http body is not required
 *
 * @author happyyangyuan
 */
public abstract class AbstractBodyNotRequiredAsyncForwarder extends AbstractAsyncForwarder implements IBodyParser {

    @Override
    protected UnitRequest bodyParams(String body, Map<String, String> headerIgnored) throws ReqBodyParseFailure {
        JSONObject fromBodyMap = parseBody(body);
        UnitRequest controllerRequest = UnitRequest.create();
        controllerRequest.setArgMap(fromBodyMap);
        return controllerRequest;
    }

    /**
     * @deprecated not used any more.
     */
    private boolean fromStandaloneNode(Map<String, String> $header) {
        return $header.get(Constant.XIAN_APPLICATION_HEADER) != null;
    }

    /**
     * @deprecated parameters started with '$' is no longer used.
     */
    private void clearParams(JSONObject jObj) {
        LOG.debug("出于安全考虑，我们要清除来自外部的'$'参数，因为那是系统级别参数。但来自standalone的节点不清除'$'参数");
        Iterator<String> it = jObj.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (key.startsWith("$")) {
                LOG.warn(String.format("外部传入了'$'开头的参数%s=%s,而'$'参数是系统内部参数,只允许内部使用!", key, jObj.get(key)), new RuntimeException());
                it.remove();
            }
        }
    }

}
