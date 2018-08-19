package info.xiancloud.core.conf.application;

import info.xiancloud.core.conf.IPropertiesReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Function;

/**
 * ApplicationConfig using properties file instead of xml file.
 * So that we can unify the configuration for system environment, system properties, plugin properties.
 *
 * @author happyyangyuan
 */
public class ApplicationConfig implements IPropertiesReader {

    private static final String PATH = "conf/application.properties";
    public static final ApplicationConfig SINGLETON = new ApplicationConfig();

    private static final Properties CACHE = new Properties();

    static {
        try (InputStream inputStream =
                     new File(PATH).exists() ?
                             // we look for configuration in conf/application.properties
                             new FileInputStream(PATH) :
                             null) {
            if (inputStream != null) {
                CACHE.load(inputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Function<String, String> _reader() {
        return CACHE::getProperty;
    }

    @Override
    public String splitter() {
        return ".";
    }
}
