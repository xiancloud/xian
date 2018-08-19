package info.xiancloud.core.log;

import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.thread.MsgIdHolder;

/**
 * print log to System.out
 *
 * @author happyyangyuan
 */
public class SystemOutLogger implements Logger {

    /**
     * indicate whether pring msg id to system out log
     */
    private static final boolean PRINT_MSG_ID = XianConfig.getBoolValue("print_msg_id_sys_out", false);

    /**
     * singleton for itself. Currently for internal usage.
     */
    public static final SystemOutLogger SINGLETON = new SystemOutLogger();

    @Override
    public void info(Object message, Throwable optional, String ignored) {
        System.out.println(prepareMessage(message, LOG.Level.INFO));
        if (optional != null) {
            optional.printStackTrace();
        }
    }

    @Override
    public void debug(Object message, Throwable optional, String ignored) {
        System.out.println(prepareMessage(message, LOG.Level.DEBUG));
        if (optional != null) {
            optional.printStackTrace();
        }
    }

    @Override
    public void warn(Object message, Throwable optional, String ignored) {
        System.err.println(prepareMessage(message, LOG.Level.WARN));
        if (optional != null) {
            optional.printStackTrace();
        }
    }

    @Override
    public void error(Object message, Throwable optional, String ignored) {
        System.err.println(prepareMessage(message, LOG.Level.ERROR));
        if (optional != null) {
            optional.printStackTrace();
        }
    }

    private String prepareMessage(Object originalMessage, LOG.Level level) {
        String fullMsg = "[" + level.name() + "] " + originalMessage;
        if (PRINT_MSG_ID) {
            fullMsg += " [msgId=" + MsgIdHolder.get() + "]";
        }
        return fullMsg;
    }

}
