package info.xiancloud.core.conf.plugin;

import info.xiancloud.core.util.StringUtil;
import info.xiancloud.core.util.StringUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * 如果将来要修改config.txt配置文件名称或者路径，此类将会排上用场，可以实现平滑过渡
 * 支持将同一个jar、module内的多个配置文件合并使用
 *
 * @author explorerlong, happyyangyuan
 */
public class PluginCompositeConfigReader extends AbstractPluginConfigReader {

    private Set<IPluginConfigReader> readers = new HashSet<>();

    public PluginCompositeConfigReader(Set<IPluginConfigReader> readers) {
        this.readers = readers;
    }

    synchronized public void reload() {
        StringBuilder contentBuffer = new StringBuilder();
        for (IPluginConfigReader reader : readers) {
            properties.putAll(reader.properties());
            if (!StringUtil.isEmpty(reader.content())) {
                contentBuffer.append(reader.content()).append(System.lineSeparator());
            }
        }
        content = contentBuffer.toString();
        loadedTime = System.currentTimeMillis();
    }

}
