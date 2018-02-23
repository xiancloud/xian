package info.xiancloud.plugin.unit.res;

import info.xiancloud.plugin.*;
import info.xiancloud.plugin.unit.ZookeeperGroup;
import info.xiancloud.plugin.distribution.res.IResAware;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.StringUtil;

/**
 * @author happyyangyuan
 */
public class GetPluginConfigUnit implements Unit {
    @Override
    public String getName() {
        return "getPluginConfig";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("现在本unit只用来做调试使用，程序内如果想要获取注册中心配置值，" +
                "请直接使用IResAware.singleton.get()")
                .setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", String.class, "配置项key，不允许为空", REQUIRED)
                .add("plugin", String.class, "插件名", REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String key = msg.get("key", String.class);
        String pluginName = msg.get("plugin", String.class);
        String value = IResAware.singleton.get(pluginName, key);
        if (StringUtil.isEmpty(value)) {
            LOG.debug("配置尚未注册到zk，因此返回null");
            return UnitResponse.failure(null, "配置尚未注册至zk，请读取本地配置代替.");
        }
        return UnitResponse.success(value);
    }

    @Override
    public Group getGroup() {
        return ZookeeperGroup.singleton;
    }
}
