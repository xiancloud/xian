package info.xiancloud.core.conf;

import info.xiancloud.core.conf.composite.CompositeConfigReader;
import info.xiancloud.core.conf.plugin.PluginConfig;
import info.xiancloud.core.conf.sysenv.SystemEnv;
import info.xiancloud.core.conf.sysproperty.SystemProperty;

/**
 * Unified configuration reader helper class.
 * Combination of all level configuration reader.<br>
 * priority: 'PluginConfig' greater than 'ApplicationConfig' greater than 'SystemProperty' greater than 'SystemEnv'.
 * <br>
 * Note:
 * 1、If you are running your code in IDE with unit test or main method and you modified the system env var, you have to restart your IDE to make the new value work.
 * 2、The key used should not contain '.', because system environment vars do not support name containing dot.
 *
 * @author happyyangyuan
 */
public abstract class XianConfig {

    /**
     * read configuration from {@linkplain PluginConfig} higher than {@linkplain} ApplicationConfig higher than {@linkplain SystemProperty} higher than {@linkplain SystemEnv}.
     *
     * @param key the key, do not use '.' in the key because system environment var does not support name containing dot.
     * @return the value
     */
    public static String get(String key) {
        return CompositeConfigReader.SINGLETON.get0(key);
    }

    /**
     * @param key           the key, do not use '.' in the key because system environment var does not support name containing dot.
     * @param defaultIfNull if configuration for the key is not found, then this default value is returned.
     * @return the configured value or the default value for null/empty configuration.
     * @see #get(String)
     */
    public static String get(String key, String defaultIfNull) {
        return CompositeConfigReader.SINGLETON.get0(key, defaultIfNull);
    }

    /**
     * split the value wiht ',' and trim all white spaces.
     *
     * @param key the key, do not use '.' in the key because system environment var does not support name containing dot.
     * @return an spitted string array
     */
    public static String[] getStringArray(String key) {
        return CompositeConfigReader.SINGLETON.getStringArray0(key);
    }

    /**
     * @param key the key, do not use '.' in the key because system environment var does not support name containing dot.
     * @see #getStringArray(String)
     */
    public static String[] getStringArray(String key, String[] defaultIfNull) {
        return CompositeConfigReader.SINGLETON.getStringArray0(key, defaultIfNull);
    }

    /**
     * @param key the key, do not use '.' in the key because system environment var does not support name containing dot.
     * @return the configured string value converted into integer object or null.
     */
    public static Integer getInteger(String key) {
        return CompositeConfigReader.SINGLETON.getInteger0(key);
    }

    /**
     * @param key the key, do not use '.' in the key because system environment var does not support name containing dot.
     * @see #getInteger(String)
     */
    public static Integer getInteger(String key, Integer defaultIfNull) {
        return CompositeConfigReader.SINGLETON.getInteger0(key, defaultIfNull);
    }

    /**
     * @param key the key, do not use '.' in the key because system environment var does not support name containing dot.
     * @return the int value or 0 if not configured
     */
    public static int getIntValue(String key) {
        return CompositeConfigReader.SINGLETON.getIntValue0(key);
    }

    /**
     * @param key the key, do not use '.' in the key because system environment var does not support name containing dot.
     * @see #getIntValue(String)
     */
    public static int getIntValue(String key, int defaultIfNull) {
        return CompositeConfigReader.SINGLETON.getIntValue0(key, defaultIfNull);
    }

    /**
     * @param key the key, do not use '.' in the key because system environment var does not support name containing dot.
     * @return the configured string value converted into Long object or null.
     */
    public static Long getLong(String key) {
        return CompositeConfigReader.SINGLETON.getLong0(key);
    }

    /**
     * @param key the key, do not use '.' in the key because system environment var does not support name containing dot.
     * @see #getLong(String)
     */
    public static Long getLong(String key, Long defaultIfNull) {
        return CompositeConfigReader.SINGLETON.getLong0(key, defaultIfNull);
    }

    /**
     * @param key the key, do not use '.' in the key because system environment var does not support name containing dot.
     * @return the long value or 0 if not configured
     */
    public static long getLongValue(String key) {
        return CompositeConfigReader.SINGLETON.getLongValue0(key);
    }

    /**
     * @param key the key, do not use '.' in the key because system environment var does not support name containing dot.
     * @see #getLongValue(String)
     */
    public static long getLongValue(String key, long defaultIfNull) {
        return CompositeConfigReader.SINGLETON.getLongValue0(key, defaultIfNull);
    }

    /**
     * @param key the key, do not use '.' in the key because system environment var does not support name containing dot.
     * @return true if the configured value equals ignore case 'true', otherwise false.
     */
    public static boolean getBoolValue(String key) {
        return CompositeConfigReader.SINGLETON.getBoolValue0(key);
    }

    /**
     * @param key the key, do not use '.' in the key because system environment var does not support name containing dot.
     * @see #getBoolValue(String)
     * @see #get(String)
     */
    public static boolean getBoolValue(String key, boolean defaultIfNull) {
        return CompositeConfigReader.SINGLETON.getBoolValue0(key, defaultIfNull);
    }

    /**
     * Returns a Boolean with a value represented by the specified configuration. The Boolean returned represents a true value if the string argument is not null and is equal, ignoring case, to the string "true".
     * If the key is not configured, then null is returned.
     *
     * @param key the key, do not use '.' in the key because system environment var does not support name containing dot.
     * @see #get(String)
     */
    public static Boolean getBoolean(String key) {
        return CompositeConfigReader.SINGLETON.getBoolean0(key);
    }

    /**
     * @param key the key, do not use '.' in the key because system environment var does not support name containing dot.
     * @see #get(String)
     * @see #getBoolean(String)
     */
    public static Boolean getBoolean(String key, Boolean defaultIfNull) {
        return CompositeConfigReader.SINGLETON.getBoolean0(key, defaultIfNull);
    }

}
