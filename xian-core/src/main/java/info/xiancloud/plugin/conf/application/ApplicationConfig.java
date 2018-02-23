package info.xiancloud.plugin.conf.application;

import info.xiancloud.plugin.conf.IPropertiesReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    /*private static final String CLASS_PATH = "application.properties"; using of class path application.properties may cause
    * configuration confusing.*/
    public static final ApplicationConfig singleton = new ApplicationConfig();

    private static final Properties cache = new Properties();

    static {
        try (InputStream inputStream = new File(PATH).exists() ?
                new FileInputStream(PATH) : null
                /*PlainFileUtil.readClasspathFileIntoStream(CLASS_PATH)*/) {
            if (inputStream != null) cache.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Function<String, String> _reader() {
        return cache::getProperty;
    }

    @Override
    public String splitter() {
        return ".";
    }
}
