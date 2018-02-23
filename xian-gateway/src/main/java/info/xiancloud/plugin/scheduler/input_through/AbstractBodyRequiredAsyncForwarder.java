package info.xiancloud.plugin.scheduler.input_through;

import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.scheduler.AbstractAsyncForwarder;

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
