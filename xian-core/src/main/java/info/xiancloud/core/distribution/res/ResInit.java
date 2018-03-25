package info.xiancloud.core.distribution.res;

import info.xiancloud.core.conf.plugin.PluginCompositeConfigReader;
import info.xiancloud.core.conf.plugin.IPluginConfigReader;
import info.xiancloud.core.conf.plugin.PluginJarResReader;
import info.xiancloud.core.conf.plugin.PluginConfig;
import info.xiancloud.core.init.Initable;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.Reflection;
import info.xiancloud.core.util.file.PluginFileUtil;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;


/**
 * 扫描到所有的jar包，得到config.txt/config.properties，得到jar包版本，如果版本有变更，那么将配置注册到注册中心；
 * 单例模式；
 * 注册中心插件需要实现该抽象类；
 *
 * @author happyyangyuan
 */
public abstract class ResInit implements Initable {

    private static final List<ResInit> resInits = Reflection.getSubClassInstances(ResInit.class);

    public static final ResInit singleton = resInits.isEmpty() ? null : resInits.get(0);

    public void init() {
        //扫描相对路径 plugins/ 得到所有的插件jar包
        for (File jar : PluginFileUtil.jars()) {
            try {
                String version = PluginFileUtil.version(jar),
                        pluginName = PluginFileUtil.pluginName(jar);
                if (isNewVersion(pluginName, version)) {
                    IPluginConfigReader reader = new PluginCompositeConfigReader(new HashSet<IPluginConfigReader>() {{
                        for (String configFile : PluginConfig.CONFIG_FILES) {
                            add(new PluginJarResReader(jar, configFile));
                        }
                    }});
                    register(pluginName, version, reader.properties());
                }
            } catch (Throwable e) {
                LOG.error(e);//log and continue the loop
            }
        }
    }

    /**
     * 标准化配置注册动作，服务注册中心插件需要实现该抽象方法，同步注册参数指定的配置至服务注册中心；
     * 注意：本方法不应当做是否应当注册配置的判断，判断逻辑已经由xian-core做过
     *
     * @param plugin     插件名
     * @param version    插件版本号
     * @param properties 配置key-value
     */
    protected abstract void register(String plugin, String version, Properties properties);

    /**
     * 判断版本号是否有升级， 是否执行注册配置的依据
     *
     * @param plugin  插件名
     * @param version 本地jar包版本号
     * @return 如果版本与注册中心版本不同，那么说明有版本升级
     */
    protected abstract boolean isNewVersion(String plugin, String version);
}
