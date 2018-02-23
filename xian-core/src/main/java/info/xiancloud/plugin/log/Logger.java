package info.xiancloud.plugin.log;

/**
 * <p>xianframe's logger interface.</p>
 * <p>Tips: do not use this interface directly for your unit code, use {@link info.xiancloud.plugin.util.LOG LOG} util instead. </p>
 *
 * @author happyyangyuan
 */
public interface Logger {

    void info(Object message, Throwable optional, String loggerName);

    void debug(Object message, Throwable optional, String loggerName);

    void warn(Object message, Throwable optional, String loggerName);

    void error(Object message, Throwable optional, String loggerName);

}
