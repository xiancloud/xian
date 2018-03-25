package info.xiancloud.gateway.scheduler.body_required;

import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.gateway.scheduler.IResponseNonThrough;

/**
 * body 透传，并返回完整的{@link UnitResponse}
 *
 * @author happyyangyuan
 */
public class BodyRequiredAsyncForwarder extends AbstractBodyRequiredAsyncForwarder implements IResponseNonThrough {
    public static BodyRequiredAsyncForwarder singleton = new BodyRequiredAsyncForwarder();
}
