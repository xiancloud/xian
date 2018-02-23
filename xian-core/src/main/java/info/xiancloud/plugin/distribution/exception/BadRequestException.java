package info.xiancloud.plugin.distribution.exception;

import info.xiancloud.plugin.Group;

/**
 * request is illegal
 *
 * @author happyyangyuan
 */
public class BadRequestException extends AbstractXianException {

    public BadRequestException(String message){
        super(message);
    }

    @Override
    public String getCode() {
        return Group.CODE_BAD_REQUEST;
    }

}
