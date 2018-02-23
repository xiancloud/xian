package info.xiancloud.plugin.log4j1;

import info.xiancloud.plugin.util.EnvUtil;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * @author happyyangyuan
 */
public class Log4j1Logger implements info.xiancloud.plugin.log.Logger {

    static {
        try {
            System.out.println("log4j1.x is initializing...");
            if (EnvUtil.isLocalServer() || EnvUtil.isIDE()) {
                ConsoleAppender appender = new ConsoleAppender();
                appender.setEncoding("utf-8");
                appender.setLayout(new PatternLayout() {{
                    String conversionPattern = "%d{HH:mm:ss} %p - %m  [%c]";
                    if (EnvUtil.isLocalServer()) {
                        conversionPattern += "[msgId:%X{msgId}]";
                    }
                    conversionPattern += "%n";
                    setConversionPattern(conversionPattern);
                }});
                appender.setName("console");
                appender.activateOptions();
                Logger.getRootLogger().addAppender(appender);
                Logger.getRootLogger().setLevel(Level.INFO);
            } else {
                System.out.println("In server runtime no log4j logs will be wrote to local files or console");
            }
            System.out.println("log4j1.x is initialized.");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void info(Object message, Throwable optional, String loggerName) {
        Logger.getLogger(loggerName).info(message, optional);
    }

    @Override
    public void debug(Object message, Throwable optional, String loggerName) {
        Logger.getLogger(loggerName).debug(message, optional);
    }

    @Override
    public void warn(Object message, Throwable optional, String loggerName) {
        Logger.getLogger(loggerName).warn(message, optional);
    }

    @Override
    public void error(Object message, Throwable optional, String loggerName) {
        Logger.getLogger(loggerName).error(message, optional);
    }

}
