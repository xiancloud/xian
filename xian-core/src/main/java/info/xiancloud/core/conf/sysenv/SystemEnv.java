package info.xiancloud.core.conf.sysenv;

import info.xiancloud.core.conf.IPropertiesReader;

import java.util.function.Function;

/**
 * @author happyyangyuan
 */
public class SystemEnv implements IPropertiesReader {

    public static final SystemEnv singleton = new SystemEnv();

    @Override
    public Function<String, String> _reader() {
        return key -> {
            //because of the system environment name with dot is not allowed,
            //here replace '.' with '_' instead.
            return System.getenv(key.replace('.', '_'));
        };
    }

    @Override
    public String splitter() {
        return "_";
    }

}
