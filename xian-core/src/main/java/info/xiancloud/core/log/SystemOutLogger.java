package info.xiancloud.core.log;

/**
 * print log to System.out
 *
 * @author happyyangyuan
 */
public class SystemOutLogger implements Logger {

    /**
     * singleton for itself. Currently for internal usage.
     */
    public static SystemOutLogger singleton = new SystemOutLogger();

    @Override
    public void info(Object message, Throwable optional, String ignored) {
        sysout(message, optional);
    }

    @Override
    public void debug(Object message, Throwable optional, String ignored) {
        sysout(message, optional);
    }

    @Override
    public void warn(Object message, Throwable optional, String ignored) {
        sysout(message, optional);
    }

    @Override
    public void error(Object message, Throwable optional, String ignored) {
        sysout(message, optional);
    }

    private void sysout(Object message, Throwable canBeNullThrowable) {
        System.out.println(message);
        if (canBeNullThrowable != null)
            canBeNullThrowable.printStackTrace();
    }

}
