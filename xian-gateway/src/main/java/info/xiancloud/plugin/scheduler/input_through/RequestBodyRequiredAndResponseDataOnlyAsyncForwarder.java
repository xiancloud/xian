package info.xiancloud.plugin.scheduler.input_through;

import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.scheduler.IResponseDataOnly;

/**
 * request: keep the body<br>
 * response: only keep the data, and evict the code and msgId properties in the {@link UnitResponse}
 *
 * @author happyyangyuan
 */
public class RequestBodyRequiredAndResponseDataOnlyAsyncForwarder extends AbstractBodyRequiredAsyncForwarder implements IResponseDataOnly {
    public static final RequestBodyRequiredAndResponseDataOnlyAsyncForwarder singleton = new RequestBodyRequiredAndResponseDataOnlyAsyncForwarder();
}
