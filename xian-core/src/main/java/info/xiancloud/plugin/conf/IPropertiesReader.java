package info.xiancloud.plugin.conf;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import info.xiancloud.plugin.util.StringUtil;

import java.util.function.Function;


/**
 * Interface for standard reader.
 *
 * @author happyyangyuan
 */
public interface IPropertiesReader extends IEnvPrefixed {

    /**
     * @return null is returned if the configuration does not exists or is configured with an empty string.
     */
    default String get0(String key) {
        String value = _reader().apply(addPrefixIfNot(key));
        if (!StringUtil.isEmpty(value))
            return value;
        value = _reader().apply(key);
        return StringUtil.isEmpty(value) ? null : value;
    }

    /**
     * @return reader function for parent template to get the config value.
     */
    Function<String, String> _reader();

    default String get0(String key, String defaultIfNull) {
        String value = get0(key);
        return StringUtil.isEmpty(value) ? defaultIfNull : value;
    }

    /**
     * @return spitted array or empty array if config does not exists.
     */
    default String[] getStringArray0(String key) {
        return StringUtil.split(get0(key), ",");
    }

    default String[] getStringArray0(String key, String[] defaultIfNull) {
        String[] config = getStringArray0(key);
        return config.length == 0 ? defaultIfNull : config;
    }

    default Integer getInteger0(String key) {
        String config = get0(key);
        if (StringUtil.isEmpty(config)) {
            return null;
        }
        return new DoubleEvaluator().evaluate(config).intValue();
    }

    default Integer getInteger0(String key, Integer defaultIfNull) {
        Integer config = getInteger0(key);
        return config == null ? defaultIfNull : config;
    }

    default int getIntValue0(String key) {
        Integer config = getInteger0(key);
        if (config == null) {
            return 0;
        }
        return config;
    }

    default int getIntValue0(String key, int defaultIfNull) {
        return getInteger0(key, defaultIfNull);
    }

    default Long getLong0(String key) {
        String config = get0(key);
        if (StringUtil.isEmpty(config)) {
            return null;
        }
        return new DoubleEvaluator().evaluate(config).longValue();
    }

    default Long getLong0(String key, Long defaultIfNull) {
        Long config = getLong0(key);
        return config == null ? defaultIfNull : config;
    }

    default long getLongValue0(String key) {
        Long config = getLong0(key);
        if (config == null) {
            return 0;
        }
        return config;
    }

    default long getLongValue0(String key, long defaultIfNull) {
        return getLong0(key, defaultIfNull);
    }


    /**
     * @see Boolean#valueOf(String)
     */
    default Boolean getBoolean0(String key) {
        return Boolean.valueOf(get0(key));
    }

    default Boolean getBoolean0(String key, Boolean defaultIfNull) {
        Boolean boolean0 = getBoolean0(key);
        return boolean0 == null ? defaultIfNull : boolean0;
    }

    /**
     * @return default false if not configured.
     */
    default boolean getBoolValue0(String key) {
        Boolean booleanObject = getBoolean0(key);
        return booleanObject == null ? false : booleanObject;
    }

    default boolean getBoolValue0(String key, boolean defaultIfNull) {
        return getBoolean0(key, defaultIfNull);
    }

}
