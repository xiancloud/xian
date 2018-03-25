package info.xiancloud.gateway.scheduler.body_required;

import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.gateway.scheduler.AbstractAsyncForwarder;

import java.util.Map;

/**
 * Keep the http body, and pass it through.
 *
 * @author happyyangyuan
 */
public abstract class AbstractBodyRequiredAsyncForwarder extends AbstractAsyncForwarder {

    @Override
    protected UnitRequest bodyParams(String body, Map<String, String> headerIgnored) {
        UnitRequest controllerRequest = UnitRequest.create();
        controllerRequest.getContext().setBody(body);
        return controllerRequest;
    }

}
