package info.xiancloud.core.distribution.res;

import info.xiancloud.core.init.Destroyable;
import info.xiancloud.core.util.Reflection;

import java.util.List;
import java.util.Properties;

/**
 * @author happyyangyuan
 * 定制统一的接口规范，资源变动感知接口
 */
public interface IResAware extends Destroyable {

    List<IResAware> resAwares = Reflection.getSubClassInstances(IResAware.class);
    IResAware singleton = resAwares.isEmpty() ? null : resAwares.get(0);

    /**
     * @param pluginName 插件名
     * @param key        配置key名
     * @return 资源配置的取值，如果当前节点尚未连接到注册中心，那么返回null，如果key插件或者key不存在，那么返回null。
     * If the key exists but value is empty, then an empty string is returned.
     */
    String get(String pluginName, String key);

    /**
     * 读取整个配置
     *
     * @param pluginName 插件名
     * @return 整个配置，如果当前节点尚未连接到注册中心，那么返回size=0的properties对象，如果key插件不存在，那么返回size=0的properties对象
     */
    Properties getAll(String pluginName);

    /**
     * @param pluginName 插件名
     * @return 注册中心内的插件版本号，目前的版本号规则见build.gradle配置；如果当前节点尚未连接到注册中心，那么返回null；如果注册中心中没有该插件配置，那么返回null
     */
    String getVersion(String pluginName);


}
