package info.xiancloud.plugin.log4j2;

import info.xiancloud.plugin.util.thread.MsgIdHolder;
import org.apache.logging.log4j.ThreadContext;

/**
 * <p>log tracking using log4j2's ThreadContext.</p>
 * See <a href=https://logging.apache.org/log4j/2.x/manual/thread-context.html>Log4j2.x ThreadContext</a>
 *
 * @author happyyangyuan
 */
public class Log4j2MsgIdHolder extends MsgIdHolder {
    @Override
    protected String get0() {
        return ThreadContext.get(MSG_ID);
    }

    @Override
    protected void clear0() {
        ThreadContext.remove(MSG_ID);
    }

    @Override
    protected void set0(String value) {
        ThreadContext.put(MSG_ID, value);
    }
}
