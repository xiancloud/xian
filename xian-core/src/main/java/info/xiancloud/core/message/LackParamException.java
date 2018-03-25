package info.xiancloud.core.message;

import java.util.Arrays;

/**
 * Exception for lack of parameters.
 *
 * @author happyyangyuan
 */
public class LackParamException extends Exception {
    private String group;
    private String unit;
    private String[] missedParams;

    public LackParamException(String group, String unit, String... missedParams) {
        this.group = group;
        this.unit = unit;
        this.missedParams = missedParams;
    }

    @Override
    public String getMessage() {
        return String.format("unit %s.%s，缺少参数: %s", group, unit, Arrays.toString(missedParams));
    }

    public String[] getLacedParams() {
        return missedParams;
    }

}
