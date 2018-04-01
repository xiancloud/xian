package info.xiancloud.core.conf.plugin;

import info.xiancloud.core.distribution.res.IResAware;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.core.util.file.PluginFileUtil;
import info.xiancloud.core.*;
import info.xiancloud.core.conf.XianConfig;

/**
 * Configuration reader for none ide environment.
 *
 * @author happyyangyuan
 */
public class PluginNoneIdeConfig extends PluginConfig implements Unit {

    @Override
    public String getName() {
        return "debugXianConfig";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("Read configuration in plugin, internally used only.").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input().add("key", String.class, "key", REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        return UnitResponse.createSuccess(XianConfig.get(msg.getString("key")));
    }

    @Override
    public Group getGroup() {
        return SystemGroup.singleton;
    }

    @Override
    String getCachedValue(String moduleLocationStr, String extendedKey, IPluginConfigReader reader) {
        if (IResAware.singleton != null) {//单机程序部署不连接注册中心的情况，singleton为null的
            String value = IResAware.singleton.get(getPluginName(moduleLocationStr), extendedKey);
            if (!StringUtil.isEmpty(value)/*value != null*/) {
                return value;
            }
        }
        // changed by yy, here if the configuration is empty( null or empty string) then read from local config instead.
        return reader.get(extendedKey);
    }

    /**
     * @return plugin name.
     */
    private static String getPluginName(String jarCanonicalPath) {
        String jarName = jarCanonicalPath.substring(jarCanonicalPath.lastIndexOf('/') + 1);
        //删除尾部-version，需要注意一点：将来的灰度发布功能，新版本插件配置默认覆盖掉旧版本插件配置
        return PluginFileUtil.pluginName(jarName);
        /*
        if (jarName.endsWith(".jar")) {
            return jarName.substring(0, jarName.length() - 4);
        } else {
            return jarName;
        }*/
    }
}
