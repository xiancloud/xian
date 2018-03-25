package info.xiancloud.core.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * Java process helper class for us to get the PID, HOSTNAME, main class etc.
 *
 * @author happyyangyuan
 */
public class JavaPIDUtil {

    private static Integer PID;
    private static String PROCESS_NAME;

    public static int getPID() {
        if (PID == null) {
            initPID();
        }
        return PID;
    }

    public static String getProcessName() {
        if (StringUtil.isEmpty(PROCESS_NAME)) {
            initPID();
        }
        return PROCESS_NAME;
    }

    private static String hostname;

    public static String getHostname() {
        if (StringUtil.isEmpty(hostname)) {
            initPID();
        }
        return hostname;
    }

    private static void initPID() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName();
        System.out.println("Java process name: " + name);
        PROCESS_NAME = name;
        int index = name.indexOf("@");
        if (index != -1) {
            PID = Integer.parseInt(name.substring(0, index));
            System.out.println("Java process id: " + PID);
            hostname = name.substring(index + 1);
            System.out.println("hostname: " + hostname);
        } else {
            throw new RuntimeException("Failed to obtain the java process id.");
        }
    }

    private static String mainClass;

    public static String getMainClass() {
        if (StringUtil.isEmpty(mainClass)) {
            //for oracle jdk/jre
            String oracleJava = System.getProperty("sun.java.command"); // like "org.x.y.Main arg1 arg2"
            if (!StringUtil.isEmpty(oracleJava)) {
                mainClass = oracleJava;
            } else {//just in case.
                for (final Map.Entry<String, String> entry : System.getenv().entrySet()) {
                    if (entry.getKey().startsWith("JAVA_MAIN_CLASS")) // like JAVA_MAIN_CLASS_13328
                        mainClass = entry.getValue();
                }
                if (StringUtil.isEmpty(mainClass)) {
                    throw new IllegalStateException("Cannot determine main class.");
                }
            }
            System.out.println("main方法 = " + mainClass);
        }
        return mainClass;
    }

    public static void main(String... args) throws UnknownHostException {
        JavaPIDUtil.getPID();
        System.out.println(InetAddress.getLocalHost().getHostName());
        System.out.println(System.getProperties());
        System.out.println(System.getenv());
        System.out.println(getMainClass());
    }
}
