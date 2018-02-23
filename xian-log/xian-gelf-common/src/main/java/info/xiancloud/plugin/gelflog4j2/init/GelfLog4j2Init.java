package info.xiancloud.plugin.gelflog4j2.init;

import biz.paluch.logging.gelf.MessageFormatEnum;
import biz.paluch.logging.gelf.log4j2.GelfLogAppender;
import biz.paluch.logging.gelf.log4j2.GelfLogField;
import info.xiancloud.plugin.conf.EnvConfig;
import info.xiancloud.plugin.distribution.LocalNodeManager;
import info.xiancloud.plugin.log.ICentralizedLogInitializer;
import info.xiancloud.plugin.util.EnvUtil;
import info.xiancloud.plugin.util.JavaPIDUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.LevelRangeFilter;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * <p>Gelf-log4j2 implementation of ICentralizedLogInitializer. The server runtime will call this class's create method before starting up.</p>
 *
 * @author happyyangyuan
 */
public class GelfLog4j2Init implements ICentralizedLogInitializer {
    /**
     * add the gelf-log4j2 appender into log4j2 context.
     */
    private void addAppender() {
        final LoggerContext context = LoggerContext.getContext(false);
        final Configuration defaultConfig = context.getConfiguration();
        String gelfInputUrl = EnvUtil.isLan() ? EnvConfig.get("gelfInputLanUrl") : EnvConfig.get("gelfInputInternetUrl");
        System.out.println("gelfInputUrl=" + gelfInputUrl);
        int gelfInputPort = EnvConfig.getIntValue("gelfInputPort");
        final GelfLogAppender appender = /* WriterAppender.createAppender(layout, null, writer, writerName, false, true)*/
                GelfLogAppender.createAppender(
                        defaultConfig,
                        "gelf",
                        LevelRangeFilter.createFilter(Level.OFF, Level.INFO, Filter.Result.ACCEPT, null),
                        new GelfLogField[]{
                                new GelfLogField("environment", EnvUtil.getEnv(), null, null),
                                new GelfLogField("application", EnvUtil.getApplication(), null, null),
                                new GelfLogField("pid", JavaPIDUtil.getProcessName(), null, null),
                                new GelfLogField("nodeId", LocalNodeManager.LOCAL_NODE_ID, null, null),
                                new GelfLogField("thread", null, null, PatternLayout.newBuilder().withAlwaysWriteExceptions(false).withPattern("%t").build()),
                                new GelfLogField("logger", null, null, PatternLayout.newBuilder().withAlwaysWriteExceptions(false).withPattern("%c").build()),
                                new GelfLogField("msgId", null, "msgId", null),
                        },
                        null,
                        null,
                        gelfInputUrl,
                        null,
                        gelfInputPort + "", null,
                        "false",
                        null,
                        null,
                        "gelf-log4j2",
                        "true",//only works when extractStackTrace is true
                        null,
                        null,
                        null,
                        true,
                        "%p %m%n",
                        MessageFormatEnum.json
                );
        if (appender == null)
            throw new RuntimeException("gelf-log4j2 fails to initialize.");
        appender.start();
        defaultConfig.addAppender(appender);
        updateLoggers(appender, defaultConfig);
    }

    private void updateLoggers(final Appender appender, final Configuration config) {
        final Level level = null;
        final Filter filter = null;
        for (final LoggerConfig loggerConfig : config.getLoggers().values()) {
            loggerConfig.addAppender(appender, level, filter);
        }
        config.getRootLogger().addAppender(appender, level, filter);
    }

    @Override
    public void init() {
        addAppender();
    }
}
