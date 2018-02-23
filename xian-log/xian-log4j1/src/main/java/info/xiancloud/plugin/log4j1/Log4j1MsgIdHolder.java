package info.xiancloud.plugin.log4j1;

import info.xiancloud.plugin.util.thread.MsgIdHolder;
import org.apache.log4j.MDC;

/**
 * <p>msg id holder log4j 1.x implementation.
 * we use log4j 1.x MDC thread local.</p>
 *
 * @author happyyangyuan
 */
public class Log4j1MsgIdHolder extends MsgIdHolder {
    @Override
    protected String get0() {
        return MDC.get(MSG_ID) == null ? null : MDC.get(MSG_ID).toString();
    }

    @Override
    protected void clear0() {
        MDC.clear();
    }

    @Override
    protected void set0(String value) {
        MDC.put(MSG_ID, value);
    }
}
