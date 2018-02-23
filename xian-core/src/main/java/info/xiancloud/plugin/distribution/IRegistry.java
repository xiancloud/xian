package info.xiancloud.plugin.distribution;

import info.xiancloud.plugin.init.Destroyable;
import info.xiancloud.plugin.init.Initable;
import info.xiancloud.plugin.util.Reflection;

import java.util.List;

/**
 * @author happyyangyuan
 * 注册中心规范接口；
 * 单例，插件必须提供实现；
 */
public interface IRegistry extends Initable, Destroyable {

    List<IRegistry> registries = Reflection.getSubClassInstances(IRegistry.class);

    /**
     * 单例
     */
    IRegistry singleton = registries.isEmpty() ? null : registries.get(0);

}
