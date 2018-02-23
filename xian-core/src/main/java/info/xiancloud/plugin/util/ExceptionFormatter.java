package info.xiancloud.plugin.util;

import com.alibaba.fastjson.JSONArray;

/**
 * @author happyyangyuan
 */
public class ExceptionFormatter {

    public static JSONArray stackTraceToJSONArray(Throwable throwable) {
        return stackTraceToJSONArray(throwable, false);
    }

    private static JSONArray stackTraceToJSONArray(Throwable throwable, boolean isCause) {
        JSONArray stackTraceArray = new JSONArray();
        if (isCause) {
            stackTraceArray.add("Caused by: " + throwable.toString());
        } else {
            stackTraceArray.add(throwable.toString());
        }
        for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
            stackTraceArray.add("   " + stackTraceElement.toString());
        }
        if (throwable.getCause() != null) {
            stackTraceArray.addAll(stackTraceToJSONArray(throwable.getCause(), true));
        }
        return stackTraceArray;
    }

}
