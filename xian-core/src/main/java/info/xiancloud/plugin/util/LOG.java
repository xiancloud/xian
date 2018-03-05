package info.xiancloud.plugin.util;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.conf.XianConfig;
import info.xiancloud.plugin.log.Logger;
import info.xiancloud.plugin.log.SystemOutLogger;

import java.util.List;

/**
 * @author happyyangyuan
 * Unified LOG util for the framework.
 * Performance has been improved for production env, we do not recursively traverse the stacktrace for the logger name.
 * This class loading is dependent on {@link XianConfig#get(String, String)} which means the xianConfig.get method should not depend on this LOG class,
 * or you will get an class loading dead lock.
 */
public abstract class LOG {

    /**
     * singleton of xianframe Logger.
     * The logger implementation must be added to classpath, otherwise an IndexOutOfBoundsException is thrown.
     */
    public static Logger singleton;

    static {
        try {
            List<Logger> loggerList = Reflection.getSubClassInstances(Logger.class);
            for (Logger logger : loggerList) {
                if (!(logger instanceof SystemOutLogger))
                    singleton = logger;
            }
            if (singleton == null)
                singleton = loggerList.get(0);
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Default global log level value is from xianConfig, use {@link #setLevel(Level)} you want to modify the log level any time anywhere.
     * Note that, the scope for this property is the current node rather than other nodes in the cluster.
     */
    private static Level level = Level.valueOf(XianConfig.get("xianLogLevel", Level.INFO.name()));

    public static void debug(Object message) {
        if (level.ordinal() >= Level.DEBUG.ordinal())
            singleton.debug(message, null, loggerName());
    }

    public static void debug(Object message, Throwable e) {
        if (level.ordinal() >= Level.DEBUG.ordinal())
            singleton.debug(message, e, loggerName());
    }

    public static void info(Object message) {
        if (level.ordinal() >= Level.INFO.ordinal())
            singleton.info(message, null, loggerName());
    }

    /**
     * print error log
     */
    public static void error(Object message) {
        if (level.ordinal() >= Level.ERROR.ordinal())
            singleton.error(message, null, loggerName());
    }

    /**
     * print error level log of the exception message with stacktrace.
     */
    public static void error(Throwable t) {
        if (level.ordinal() >= Level.ERROR.ordinal())
            singleton.error(null, t, loggerName());
    }

    /**
     * print error level log of the the errMsg followed by the exception message with stacktrace.
     */
    public static void error(Object errMsg, Throwable t) {
        if (level.ordinal() >= Level.ERROR.ordinal())
            singleton.error(errMsg, t, loggerName());
    }

    /**
     * print warning level log of the exception message with stacktrace.
     */
    public static void warn(Object warnMsg) {
        if (level.ordinal() >= Level.WARN.ordinal())
            if (warnMsg instanceof Throwable) {
                singleton.warn(null, (Throwable) warnMsg, loggerName());
            } else {
                singleton.warn(warnMsg, null, loggerName());
            }
    }

    /**
     * print warning level log of the the errMsg followed by the exception message with stacktrace.
     */
    public static void warn(Object warnMsg, Throwable t) {
        if (level.ordinal() >= Level.WARN.ordinal())
            singleton.warn(warnMsg, t, loggerName());
    }

    /**
     * print monitoring log
     *
     * @param type        some name, whatever.
     * @param costInMilli the named operation's cost time in millisecond.
     */
    public static void cost(String type, long costInMilli) {
        info(new JSONObject() {{
            put("type", type);
            put("cost", costInMilli);
        }});
    }

    /**
     * print monitoring log.
     *
     * @param type        some name, whatever.
     * @param startInNano the relative jvm time in nano when the operation starts.
     * @param endInNano   the relative jvm time in nano when the operation ends.
     * @see System#nanoTime()
     */
    public static void cost(String type, long startInNano, long endInNano) {
        cost(type, (endInNano - startInNano) / 1000000);
    }

    /**
     * getCallerFromTheCurrentStack containing full class name, method name and line number of the java file name.
     *
     * @deprecated this may reduce the performance.
     */
    private static String getCallerFromTheCurrentStack() {
        StackTraceElement[] stes = new Throwable().getStackTrace();
        for (StackTraceElement ste : stes) {
            String stackLine = ste.toString();
            if (!stackLine.contains(LOG.class.getName())) {
                return stackLine;
            }
        }
        return stes[stes.length - 1].toString();
    }

    private static String loggerName() {
        if (EnvUtil.isProduction())
            // For performance consideration, we do not traverse the stacktrace for logger name.
            return null;
        return getCallerFromTheCurrentStack();
    }

    /**
     * @return true if debug is enabled, false otherwise.
     */
    public static boolean isDebugEnabled() {
        return level.ordinal() <= Level.DEBUG.ordinal();
    }

    /**
     * specify the highest log level.
     * This method is for configuration use.
     *
     * @param level the level enum.
     */
    public static void setLevel(Level level) {
        LOG.level = level;
    }

    public enum Level {
        /**
         * No events will be logged.
         */
        OFF,
        /**
         * A severe error that will prevent the application from continuing.
         */
        FATAL,
        /**
         * An error in the application, possibly recoverable.
         */
        ERROR,
        /**
         * An event that might possible lead to an error.
         */
        WARN,
        /**
         * An event for informational purposes.
         */
        INFO,
        /**
         * A general debugging event.
         */
        DEBUG,
        /**
         * A fine-grained debug message, typically capturing the flow through the application.
         */
        TRACE,
        /**
         * All events should be logged.
         */
        ALL,
    }
}
