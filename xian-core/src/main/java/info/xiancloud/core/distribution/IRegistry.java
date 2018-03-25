package info.xiancloud.core.distribution;

import info.xiancloud.core.init.Destroyable;
import info.xiancloud.core.init.Initable;
import info.xiancloud.core.util.Reflection;

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
