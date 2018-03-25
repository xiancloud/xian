package info.xiancloud.core.conf.plugin;

import info.xiancloud.core.conf.composite.CompositeConfigReader;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.conf.IEnvPrefixed;
import info.xiancloud.core.conf.IPropertiesReader;
import info.xiancloud.core.conf.composite.CompositeConfigReader;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * <p>configuration center is supported.</p>
 * <p>Plugin level configuration level reader.</p>
 *
 * @author happyyangyuan
 */
public abstract class PluginConfig implements IPropertiesReader {

    private static Map<String, IPluginConfigReader> readersMap = new ConcurrentHashMap<>();
    private static final Object lock = new Object();
    private static final String XIAN_CORE_LOCATION = PluginConfig.class.getProtectionDomain().getCodeSource().getLocation().toExternalForm();
    public static final String[] CONFIG_FILES = new String[]{"plugin.properties", "config.properties", "config.txt"/*config.properties, config.txt are for downward compatibility only, not recommended*/};
    private static Method SUN_REFLECTION_GETCALLERCLASS_METHOD;

    static {
        try {
            Class<?> reflectionClass = Class.forName("sun.reflect.Reflection");
            SUN_REFLECTION_GETCALLERCLASS_METHOD = reflectionClass.getDeclaredMethod("getCallerClass", int.class);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public String get0(String key) {
        Class caller = /*getCaller*/getCallerUsingSunReflection();
        String value = getByCaller(addPrefixIfNot(key), caller);
        if (!StringUtil.isEmpty(value))
            return value;
        value = getByCaller(key, caller);
        return StringUtil.isEmpty(value) ? null : value;
    }

    /**
     * getCachedValue, subclass need to implement this method.
     *
     * @param moduleLocationStr plugin path
     * @param key               key
     * @param reader            config cache
     * @return the value
     */
    abstract String getCachedValue(String moduleLocationStr, String key, IPluginConfigReader reader);

    /**
     * 根据调用者所属模块，获取对应模块配置文件的配置项值。
     * 请不要在其他类中二次封装该工具方法,它依赖堆栈来定位调用者的module
     */
    @Override
    public Function<String, String> _reader() {
        throw new RuntimeException("forbidden");
    }

    @Override
    public String splitter() {
        return ".";
    }

    private static Class getCallerUsingSunReflection() {
        Class caller;
        int i = 5;//depth=5 by experimental.
        do {
            /**
             sun reflection api is used, maybe some future jdk release won't support.
             performance：
             Log4j2StackLocatorUtil: 649.568976 ms.
             Reflection: 346.145105 ms.
             Current Thread StackTrace: 6454.921876 ms.
             Throwable StackTrace: 4005.284817 ms.
             SecurityManager: 772.823546 ms.
             @see ：TestGetCallerClassName.java
             */
            try {
                caller = (Class) SUN_REFLECTION_GETCALLERCLASS_METHOD.invoke(null, i) /*Reflection.getCallerClass(i)*/;
                /*System.out.println(caller);*/
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            i++;
        }
        while (caller == PluginConfig.class || caller == CompositeConfigReader.class || caller == XianConfig.class
                || caller == IPropertiesReader.class || caller == IEnvPrefixed.class);
        return caller;
    }

    private static String getByCaller(String key, Class caller) {
        String value;
        URL moduleLocation = caller.getProtectionDomain().getCodeSource().getLocation();
        String moduleLocationStr = moduleLocation.toExternalForm();
        if (readersMap.get(moduleLocationStr) == null) {
            synchronized (lock) {
                if (!readersMap.containsKey(moduleLocationStr)) {
                    IPluginConfigReader reader = new PluginCompositeConfigReader(new HashSet<IPluginConfigReader>() {{
                        for (String configFile : CONFIG_FILES) {
                            add(new PluginBaseConfigReader(caller, configFile));
                        }
                    }});
                    readersMap.put(moduleLocationStr, reader);
                    /*singleton().writeToZk(moduleLocationStr, reader.properties());
                    不再在这里写入到配置注册中心内*/
                }
            }
        }
        IPluginConfigReader reader = readersMap.get(moduleLocationStr);
        String extendedKey = extendedKey(reader.properties(), key);
        value = PluginConfigHolder.singleton.getCachedValue(moduleLocationStr, extendedKey, reader);
        if (StringUtil.isEmpty(value)) {
            if (!XIAN_CORE_LOCATION.equals(moduleLocationStr)) {
                //read from xian-core then.
                value = getByCaller(key, PluginConfig.class);
            }
        }
        return value;
    }

    /**
     * For internal use only, please do not rely on this method.
     *
     * @return the environment specific configuration key. The original key will be returned if no prefixed configuration is specified for the current env.
     */
    private static String extendedKey(Properties properties, String key) {
        String extendedKey = extendedKey(key);
        return properties.containsKey(extendedKey) ? extendedKey : key;
    }

    /**
     * For internal use only, please do not rely on this method.
     *
     * @return the environment specific configuration key.
     */
    private static String extendedKey(String key) {
        return EnvUtil.getShortEnvName() + "." + key;
    }

    //==================================================testing code here===============================================

    /**
     * @deprecated pool performance, use {@link #getCallerUsingSunReflection()} instead.
     */
    private static Class getCaller() {
        StackTraceElement[] stes = new Throwable().getStackTrace();
        try {
            for (int i = 1; i < stes.length; i++) {
                StackTraceElement ste = stes[i];
                if (!ste.getClassName().equals(PluginConfig.class.getName())) {
                    return Class.forName(ste.getClassName());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("plugin configuration reading failure - failed to get the caller", e);
        }
        throw new RuntimeException("plugin configuration reading failure - failed to get the caller");
    }

    private static String performanceTestGetConfigUsingStackTrace(String key) {
        Class caller = getCaller();
        return getByCaller(key, caller);
    }

    private static String performanceTestGetConfigUsingSunReflection(String key) {
        Class caller = getCallerUsingSunReflection();
        return getByCaller(key, caller);
    }

    public static String[] performanceTestGetStringArrayUsingStackTrace(String key) {
        return StringUtil.split(performanceTestGetConfigUsingStackTrace(key), ",");
    }

    public static String[] performanceTestGetStringArrayUsingSunReflection(String key) {
        return StringUtil.split(performanceTestGetConfigUsingSunReflection(key), ",");
    }

}
