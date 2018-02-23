package info.xiancloud.plugin.scheduler;

import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.util.ExceptionFormatter;

/**
 * @author happyyangyuan
 */
public interface IResponseNonThrough extends IResponseExtractor {
    default String extractContext(UnitResponse unitResponse) {
        if (unitResponse.getData() != null && unitResponse.getData() instanceof Throwable) {
            //here in api gateway, format the exception object to stack trace string array.
            unitResponse.setData(ExceptionFormatter.stackTraceToJSONArray(unitResponse.getData()));
            //make the exception response beautiful
            unitResponse.getContext().setPretty(true);
        }
        return unitResponse.toVoJSONString();
    }
}
