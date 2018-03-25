package info.xiancloud.core.conf.plugin;

/**
 * Configuration reader for IDE
 *
 * @author happyyangyuan
 */
class PluginIdeConfig extends PluginConfig {

    @Override
    String getCachedValue(String moduleLocationStr, String extendedKey, IPluginConfigReader reader) {
        return reader.get(extendedKey);
    }
}
