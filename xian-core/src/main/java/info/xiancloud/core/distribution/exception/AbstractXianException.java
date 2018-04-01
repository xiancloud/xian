package info.xiancloud.core.distribution.exception;

import info.xiancloud.core.message.UnitResponse;

/**
 * 异常父类模板
 *
 * @author happyyangyuan
 */
public abstract class AbstractXianException extends Exception {

    public AbstractXianException() {
    }

    public AbstractXianException(String message) {
        super(message);
    }

    public UnitResponse toUnitResponse() {
        return UnitResponse.createError(getCode(), this, getLocalizedMessage());
    }

    public abstract String getCode();

}
