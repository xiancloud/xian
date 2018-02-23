package info.xiancloud.plugin.conf.plugin;

import java.util.Properties;

/**
 * 资源读取父类
 *
 * @author happyyangyuan
 */
public abstract class AbstractPluginConfigReader implements IPluginConfigReader {
    protected Properties properties = new Properties();
    protected String content = "";
    protected long loadedTime = Long.MAX_VALUE;
    protected String resource;//资源文件名，classpath相对路径

    @Override
    public Properties properties() {
        if (!loaded()) {
            reload();
        }
        return properties;
    }

    @Override
    public String content() {
        if (!loaded()) {
            reload();
        }
        return content;
    }

    @Override
    public long getLoadedTime() {
        return loadedTime;
    }

    @Override
    public String resource() {
        return resource;
    }
}
