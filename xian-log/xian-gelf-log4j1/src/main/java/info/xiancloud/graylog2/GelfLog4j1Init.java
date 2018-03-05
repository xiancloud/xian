package info.xiancloud.graylog2;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.conf.XianConfig;
import info.xiancloud.plugin.distribution.LocalNodeManager;
import info.xiancloud.plugin.log.ICentralizedLogInitializer;
import info.xiancloud.plugin.util.EnvUtil;
import info.xiancloud.plugin.util.JavaPIDUtil;
import info.xiancloud.plugin.util.LOG;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.graylog2.log.GelfAppender;
import org.graylog2.log.GelfJsonAppender;

/**
 * <p>create or destroy gelf-log4j1 client</p>
 *
 * @author happyyangyuan
 */
public class GelfLog4j1Init implements ICentralizedLogInitializer {

    private static final String GELF_APPENDER_NAME = "graylog2";
    private static boolean initialized = false;

    @Override
    public void init() {
        try {
            init0();
        } catch (AlreadyInitializedException e) {
            throw new RuntimeException("Gelf-log4j1 failed to initialize.", e);
        }
    }

    public static final class AlreadyInitializedException extends Exception {
        @Override
        public String getMessage() {
            return "graylog is already initialized!";
        }
    }

    public static final class AlreadyDestroyedException extends Exception {
        @Override
        public String getMessage() {
            return "graylog is already destroyed!";
        }
    }

    synchronized public static void init0() throws AlreadyInitializedException {
        if (initialized) {
            throw new AlreadyInitializedException();
        }
        System.out.println("Starting graylog client...");
        /*GraylogUdpConnPool.create();*/
        addGelfAppender();
        initialized = true;
        LOG.info("graylog client started.");
    }

    synchronized public static void destroy() throws AlreadyDestroyedException {
        if (!initialized) {
            throw new AlreadyDestroyedException();
        }
        LOG.info("destroying graylog client...");
        /*GraylogUdpConnPool.destroy();*/
        removeGelfAppender();
        initialized = false;
        System.out.println("graylog client is shutdown. (this log cannot be sent to graylog server.)");
    }

    private static void addGelfAppender() {
        GelfAppender appender = new GelfJsonAppender/*GelfAppender GelfJsonAppender*/();
        appender.setName(GELF_APPENDER_NAME);
        if (EnvUtil.isLan()) {
            appender.setGraylogHost(XianConfig.get("gelfInputLanUrl"));
        } else {
            appender.setGraylogHost(XianConfig.get("gelfInputInternetUrl"));
        }
        appender.setGraylogPort(XianConfig.getIntValue("gelfInputInternetUrl"));
        appender.setFacility("gelf-java");
        appender.setLayout(new PatternLayout() {{
            setConversionPattern("%p %m");
        }});
        appender.setExtractStacktrace(true);
        appender.setIncludeLocation(false);
        JSONObject additionalFields = new JSONObject() {{
            put("environment", EnvUtil.getEnv());
            put("application", EnvUtil.getApplication());
            put("pid", JavaPIDUtil.getProcessName());
            put("nodeId", LocalNodeManager.LOCAL_NODE_ID);
        }};
        appender.setAddExtendedInformation(true);
        appender.setAdditionalFields(additionalFields.toJSONString());
        appender.setOriginHost(JavaPIDUtil.getHostname());
        appender.activateOptions();//让以上设置生效
        Logger.getRootLogger().addAppender(appender);
    }

    private static void removeGelfAppender() {
        Logger.getRootLogger().removeAppender(GELF_APPENDER_NAME);
    }

}
