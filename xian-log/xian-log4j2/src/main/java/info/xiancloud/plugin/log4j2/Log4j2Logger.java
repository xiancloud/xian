package info.xiancloud.plugin.log4j2;

import info.xiancloud.plugin.log.Logger;
import info.xiancloud.plugin.util.EnvUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.ConfigurationFactory;

/**
 * @author happyyangyuan
 */
public class Log4j2Logger implements Logger {

    static {
        try {
            //because log4j is not initialized yet, we can only print log using system.out.
            System.out.println("log4j2.x is initializing...");
            if (EnvUtil.isLocalServer() || EnvUtil.isIDE()) {
                ConfigurationFactory.setConfigurationFactory(new CustomConfigurationFactoryWithConsoleAppenderEnabled());
            } else {
                System.out.println("In server runtime no log4j logs will be written to local files or console");
                ConfigurationFactory.setConfigurationFactory(new CustomConfigurationFactoryWithConsoleAppenderDisabled());
            }
            System.out.println("log4j2.x is initialized.");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void info(Object message, Throwable optional, String loggerName) {
        LogManager.getLogger(loggerName).info(message, optional);
    }

    @Override
    public void debug(Object message, Throwable optional, String loggerName) {
        LogManager.getLogger(loggerName).debug(message, optional);
    }

    @Override
    public void warn(Object message, Throwable optional, String loggerName) {
        LogManager.getLogger(loggerName).warn(message, optional);
    }

    @Override
    public void error(Object message, Throwable optional, String loggerName) {
        LogManager.getLogger(loggerName).error(message, optional);
    }

}
