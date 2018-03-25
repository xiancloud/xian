package info.xiancloud.core.conf.sysproperty;

import info.xiancloud.core.conf.IPropertiesReader;
import info.xiancloud.core.conf.plugin.PluginConfig;

import java.util.function.Function;

/**
 * <p>Operation System level configuration reader</p>
 * <p>By now all OS level configuration is provided by the os environment variables</p>
 * <p>It is recommended to use {@link PluginConfig} prior to Global level configuration.</p>
 * <p>
 * <p>
 * <p>
 * Read environment variable of the operation system.
 * <p>
 * You can define env prefixed os var names. <br>
 * eg.  <br>
 * export dev_port=9124 <br>
 * export production_port=9123 <br>
 * export port=9125 <br>
 * etc.
 * </p>
 *
 * @author happyyangyuan
 */
public class SystemProperty implements IPropertiesReader {

    public static final SystemProperty singleton = new SystemProperty();

    @Override
    public Function<String, String> _reader() {
        return System::getProperty;
    }

    @Override
    public String splitter() {
        return ".";
    }
}
