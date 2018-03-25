package info.xiancloud.gateway.scheduler.body_not_required;

import info.xiancloud.core.distribution.exception.BadRequestException;

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
