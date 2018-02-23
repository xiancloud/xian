package info.xiancloud.plugin.scheduler.non_input_through;

import info.xiancloud.plugin.scheduler.IResponseDataOnly;

/**
 * 1、request body is not required
 * 2、response only the data in the {@link info.xiancloud.plugin.message.UnitResponse}
 *
 * @author happyyangyuan
 */
public class RequestBodyNotRequiredAndDataOnlyResponseAsyncForwarder extends AbstractBodyNotRequiredAsyncForwarder implements IJsonBody, IResponseDataOnly {

}
