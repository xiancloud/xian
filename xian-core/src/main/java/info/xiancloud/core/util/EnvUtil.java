package info.xiancloud.core.util;

import com.alibaba.fastjson.JSON;
import info.xiancloud.core.util.file.PlainFileUtil;
import info.xiancloud.core.Constant;
import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.util.file.PlainFileUtil;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Environment utility.
 *
 * @author happyyangyuan
 */
public class EnvUtil {
    private static final String RUNTIME_PREFIX = "xian_runtime_";
    public static final String PREDEV = RUNTIME_PREFIX + "predev";
    public static final String DEV = RUNTIME_PREFIX + "dev";
    public static final String TEST = RUNTIME_PREFIX + "test";
    public static final String PREPRODUCTION = RUNTIME_PREFIX + "preproduction";
    public static final String PRODUCTION = RUNTIME_PREFIX + "production";
    private static final String IDE = RUNTIME_PREFIX + "IDE";
    /**
     * The config key of lan reference host.
     */
    public static final String LAN_REFERENCE_HOST_CONFIG = "LAN_REFERENCE_HOST";
    /**
     * This is the default lan reference host for xian framework test only.
     * Remember to modify this configuration in conf/application.properties or os env.
     */
    public static final String DEFAULT_LAN_REFERENCE_HOST = "lan.xiancloud.info";

    private static String env;
    private static String application;
    private static Boolean lan;
    private static Boolean dao;
    private static Boolean remoteSenderDisabled;

    /**
     * @return Returns the environment name, eg. {@link #PRODUCTION xian_runtime_production}, {@link #TEST xian_runtime_test} etc.
     * <p>Priority: System Variable '$XIAN_ENV' greater than working dir</p>
     */
    public static String getEnv() {
        if (StringUtil.isEmpty(env)) {
            String OS_ENV__XIAN_ENV = "XIAN_ENV";
            if (!StringUtil.isEmpty(System.getenv(OS_ENV__XIAN_ENV))) {
                env = System.getenv(OS_ENV__XIAN_ENV).startsWith(RUNTIME_PREFIX) ? System.getenv(OS_ENV__XIAN_ENV) :
                        RUNTIME_PREFIX + System.getenv(OS_ENV__XIAN_ENV);
            } else {
                String userDir = isTomcatApplication() ? getCatalinaHome() : System.getProperty("user.dir");
                if (!userDir.contains("xian_runtime")) {
                    env = IDE + "_" + JavaPIDUtil.getHostname();
                } else {
                    if (userDir.contains(RUNTIME_PREFIX)) {
                        int startIndex = userDir.indexOf(RUNTIME_PREFIX);
                        int endIndex = userDir.indexOf(File.separatorChar, startIndex);
                        env = userDir.substring(startIndex, endIndex);
                    } else {
                        env = getLocalShellStartedEnv();
                    }
                }
            }
            System.out.println("[ENV] environment = " + env);
        }
        return env;
    }

    /**
     * Determines your application runs in IDE or not. IDE is such as idea/eclipse.
     */
    public static boolean isIDE() {
        return getEnv().startsWith(IDE);
    }

    /**
     * @return Return the cached application name. Application name is same as the working directory name.
     */
    public static String getApplication() {
        if (StringUtil.isEmpty(application)) {
                /*yy将服务器环境获取xian framework application名称的方式改为不依赖xian-core.jar位置,理由是xian-core-version.jar会被抽到application外面去,比如抽到libs内.
                final File f = new File(EnvUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath());
                String filePath = f.getCanonicalPath();*/
            try {
                String userDir = System.getProperty("user.dir");
                if (isIDE()) {
                    application = "IDE";// eclipse/idea内运行的main程序或者单元测试,无法区分其application，因此统一是IDE
                } else {
                    //Application name is same as the working directory name.
                    application = userDir.substring(userDir.lastIndexOf(File.separatorChar) + 1);
                }
            } catch (Throwable e) {
                throw new RuntimeException("Unable to get application name!", e);
            }
        }
        return application;
    }

    /**
     * Check if this application runs with tomcat.
     */
    public static boolean isTomcatApplication() {
        return !StringUtil.isEmpty(getCatalinaHome());
    }

    private static String getCatalinaHome() {
        return System.getProperty("catalina.home");
    }

    /**
     * @deprecated Please use {@link #isLocalServer()} instead.
     */
    public static boolean isCustomEnv() {
        return isLocalServer();
    }

    /**
     * @return true if this process runs in your local machine (your pc for example)
     */
    public static boolean isLocalServer() {
        return getEnv().equals(getLocalShellStartedEnv());
    }

    private static String getLocalShellStartedEnv() {
        return RUNTIME_PREFIX + JavaPIDUtil.getHostname() + "_local";
    }

    /**
     * @return The dependent application names. Note that, order of this array is used.
     */
    public static String[] getDependentApplications() {
        return XianConfig.getStringArray("dependentApplications");
    }

    /**
     * @return Short environment name，eg. dev、predev、production、ide、local.
     */
    public static String getShortEnvName() {
        if (isIDE()) {
            return "ide";
        }
        if (isLocalServer()) {
            return "local";
        }
        //xian_runtime_**;
        return getEnv().substring(RUNTIME_PREFIX.length());
    }

    public static String getJreDetail() {
        StringBuilder sb = new StringBuilder();
        sb.append("SystemProperties = \r\n").append(JSON.toJSONString(System.getProperties(), true)).append("\r\n\r\n")
                .append("SystemEnv = \r\n").append(JSON.toJSONString(System.getenv(), true)).append("\r\n");
        return sb.toString();
    }

    /**
     * Check if your application runs in the LAN network environment. See {@link #LAN_REFERENCE_HOST_CONFIG} and {@link #DEFAULT_LAN_REFERENCE_HOST}
     */
    public static boolean isLan() {
        if (lan == null) {
            String lanReferenceHost = null;
            try {
                lanReferenceHost = XianConfig.get(LAN_REFERENCE_HOST_CONFIG, DEFAULT_LAN_REFERENCE_HOST);
                System.out.println(LAN_REFERENCE_HOST_CONFIG + "=" + lanReferenceHost);
                InetAddress build = Inet4Address.getByName(lanReferenceHost);
                lan = build.isReachable(500);
            } catch (UnknownHostException unknownHost) {
                System.out.println("Unknown host: " + lanReferenceHost + ". So we judge the current network as non-lan.");
                lan = false;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("LAN=" + lan);
        }
        return lan;
    }

    /**
     * Check whether or not your application runs in tencent yun 's lan network.
     *
     * @deprecated For common usability, do not use this method.
     * use {@link #isLan()} instead.
     */
    public static boolean isQcloudLan() {
        return isLan();
    }

    /**
     * Production safety check. Protection for production env.
     */
    public static boolean verifyEnvironment() {
        if (!PRODUCTION.equals(getEnv())) {
            return true;
        } else {
            File tokenFile = new File("/etc/xian/xian_runtime_production.token");
            if (tokenFile.exists() && tokenFile.isFile()) {
                String token = PlainFileUtil.readAll(tokenFile);
                return "cbab75c745ac9707cf75b719a76e81284abc04c0ae96e2506fd247e9a3d9ca04".equals(token);
            }
            return false;
        }
    }

    /**
     * @return true if this application is an application for accessing the database directly.
     * If there is xian-dao.jar in plugins dir，then we judge it as dao application node.
     * This value is cached and lazy-create.
     */
    public static boolean isDao() {
        if (dao == null) {
            try {
                Class.forName(Constant.DAO_GROUP_FULL_CLASSNAME);
                dao = true;
            } catch (ClassNotFoundException e) {
                dao = false;
            }
        }
        return dao;
    }

    /**
     * @return the cached dependent application set.
     */
    public static Set<String> getDependencies() {
        Set<String> dependencies = new HashSet<>();
        for (String mappedDbApplication : getDependentApplications()) {
            if (!Objects.equals(mappedDbApplication, getApplication())) {
                dependencies.add(mappedDbApplication);
            }
        }
        return dependencies;
    }

    public static boolean isRemoteSenderDisabled() {
        if (remoteSenderDisabled == null) {
            remoteSenderDisabled = XianConfig.getBoolValue("applicationRemoteSenderDisabled", false);
        }
        return remoteSenderDisabled;
    }

    private static InetAddress localInetAddress;

    /**
     * @return The local hostname mapped ip address if available, or 127.0.0.1.
     * <p>For linux，you need to configure the '/etc/hosts' file with a local hostname mapping the ip address, if not, you will get '127.0.0.1' returned.</p>
     * <p>For Mac OS, just as is, your local lan address is returned, nothing needs to be configured. </p>
     */
    public static String getLocalIp() {
        if (localInetAddress == null) {
            try {
                localInetAddress = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }
        return localInetAddress.getHostAddress();
    }

    /**
     * @return true for production environment, else false.
     */
    public static boolean isProduction() {
        return PRODUCTION.equals(getEnv());
    }

}
