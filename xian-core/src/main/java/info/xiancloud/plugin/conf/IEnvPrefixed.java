package info.xiancloud.plugin.conf;

import info.xiancloud.plugin.util.EnvUtil;

/**
 * Interface for standard env prefixed reader
 *
 * @author happyyangyuan
 */
public interface IEnvPrefixed {

    /**
     * @return env prefixed key.
     * attention: if the key is already prefixed, then return it as the same.
     */
    default String addPrefixIfNot(String key) {
        if (isPrefixed(key)) return key;
        return EnvUtil.getShortEnvName() + splitter() + key;
    }

    /**
     * judge whether the key is already prefixed with env.
     */
    default boolean isPrefixed(String key) {
        return key.startsWith(EnvUtil.getShortEnvName() + splitter());
    }

    /**
     * splitter of prefix and the key.
     * use '-' or '.'
     */
    String splitter();

}
