package info.xiancloud.plugin.distribution.exception;

import info.xiancloud.plugin.message.UnitResponse;

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
        return UnitResponse.error(getCode(), this, getLocalizedMessage());
    }

    public abstract String getCode();

}
