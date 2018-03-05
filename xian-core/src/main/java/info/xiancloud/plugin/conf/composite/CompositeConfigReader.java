package info.xiancloud.plugin.conf.composite;

import info.xiancloud.plugin.conf.IPropertiesReader;
import info.xiancloud.plugin.conf.XianConfig;
import info.xiancloud.plugin.conf.application.ApplicationConfig;
import info.xiancloud.plugin.conf.plugin.PluginConfigHolder;
import info.xiancloud.plugin.conf.sysenv.SystemEnv;
import info.xiancloud.plugin.conf.sysproperty.SystemProperty;

import java.util.function.Function;

/**
 * @author happyyangyuan
 * @see XianConfig
 */
public class CompositeConfigReader implements IPropertiesReader {

    public static final CompositeConfigReader singleton = new CompositeConfigReader();

    @Override
    public String get0(String key) {
        if (key.contains(".")) {
            //  Configuration key containing '.' is not recommended.
            //  If '.' is must, the system environment name will escape it to '_'
            //  Dot not print log here because this method is called under high frequency.
            //  It is warned not to use LOG util too.
            //  Do not delete this note!!!
        }
        String value = PluginConfigHolder.singleton.get0(key);
        if (value != null) return value;
        value = ApplicationConfig.singleton.get0(key);
        if (value != null) return value;
        value = SystemProperty.singleton.get0(key);
        if (value != null) return value;
        return SystemEnv.singleton.get0(key);
    }

    @Override
    public Function<String, String> _reader() {
        throw new RuntimeException("Forbidden.");
    }

    @Override
    public String splitter() {
        throw new RuntimeException("Forbidden.");
    }
}
