package info.xiancloud.gateway.scheduler.body_not_required;

import info.xiancloud.gateway.scheduler.IResponseNonThrough;

/**
 * 非透传型，json入参，output出参
 *
 * @author happyyangyuan
 */
public class DefaultAsyncForwarder extends AbstractBodyNotRequiredAsyncForwarder implements IJsonBody, IResponseNonThrough {

    public static final DefaultAsyncForwarder singleton = new DefaultAsyncForwarder();

}
