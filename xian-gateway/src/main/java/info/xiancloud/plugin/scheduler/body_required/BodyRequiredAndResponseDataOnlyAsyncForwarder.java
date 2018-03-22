package info.xiancloud.plugin.scheduler.body_required;

import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.scheduler.IResponseDataOnly;

/**
 * request: keep the body<br>
 * response: only keep the data, and evict the code and msgId properties in the {@link UnitResponse}
 *
 * @author happyyangyuan
 */
public class BodyRequiredAndResponseDataOnlyAsyncForwarder extends AbstractBodyRequiredAsyncForwarder implements IResponseDataOnly {
    public static final BodyRequiredAndResponseDataOnlyAsyncForwarder singleton = new BodyRequiredAndResponseDataOnlyAsyncForwarder();
}
