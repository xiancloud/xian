package info.xiancloud.plugin.conf.plugin;

import java.util.Properties;

/**
 * @author explorerlong
 */
public interface IPluginConfigReader {
    /**
     * 请实现为懒加载+缓存机制
     *
     * @return 键值对Properties
     */
    Properties properties();

    /**
     * 请实现为懒加载+缓存机制
     *
     * @return 配置文件内容
     */
    String content();

    /**
     * 懒加载+缓存机制<br>
     * 配置文件内key对应的value，如果不存在配置，那么返回null
     *
     * @param name key
     */
    default String get(String name) {
        return properties().getProperty(name);
    }

    /**
     * 懒加载+缓存机制<br>
     * 配置文件内key对应的value，如果不存在配置，那么返回null
     *
     * @param name         key
     * @param defaultValue 默认值
     */
    default String get(String name, String defaultValue) {
        return properties().getProperty(name, defaultValue);
    }

    /**
     * @return 配置初始化完成的时间，如果配置文件未加载，那么返回Long.MAX_VALUE
     */
    long getLoadedTime();

    /**
     * @return 配置文件是否已加载完毕
     */
    default boolean loaded() {
        return Long.MAX_VALUE != getLoadedTime();
    }

    /**
     * 懒加载，如果已经加载
     */
    default void lazyLoad() {
        if (!loaded()) {
            reload();
        }
    }

    /**
     * 重新加载配置，会覆盖原有配置，请谨慎使用；子类需要实现并发安全机制。
     */
    void reload();

    /**
     * @return 资源文件在路径
     */
    String resource();

}
