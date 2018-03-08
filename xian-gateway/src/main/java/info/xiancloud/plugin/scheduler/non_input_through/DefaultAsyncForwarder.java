package info.xiancloud.plugin.scheduler.non_input_through;

import info.xiancloud.plugin.scheduler.IResponseNonThrough;

/**
 * 非透传型，json入参，output出参
 *
 * @author happyyangyuan
 */
public class DefaultAsyncForwarder extends AbstractBodyNotRequiredAsyncForwarder implements IJsonBody, IResponseNonThrough {

    public static final DefaultAsyncForwarder singleton = new DefaultAsyncForwarder();

}
