package info.xiancloud.plugin.scheduler.non_input_through;

import info.xiancloud.plugin.distribution.exception.BadRequestException;

/**
 * the http request body parsing failure exception.
 *
 * @author happyyangyuan
 */
public class ReqBodyParseFailure extends BadRequestException {
    public ReqBodyParseFailure(String reason) {
        super(reason);
    }
}
