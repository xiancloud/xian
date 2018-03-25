package info.xiancloud.gateway.scheduler.body_required;

import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.gateway.scheduler.IResponseDataOnly;

/**
 * request: keep the body<br>
 * response: only keep the data, and evict the code and msgId properties in the {@link UnitResponse}
 *
 * @author happyyangyuan
 */
public class BodyRequiredAndResponseDataOnlyAsyncForwarder extends AbstractBodyRequiredAsyncForwarder implements IResponseDataOnly {
    public static final BodyRequiredAndResponseDataOnlyAsyncForwarder singleton = new BodyRequiredAndResponseDataOnlyAsyncForwarder();
}
