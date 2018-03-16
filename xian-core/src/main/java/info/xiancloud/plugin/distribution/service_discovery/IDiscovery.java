package info.xiancloud.plugin.distribution.service_discovery;

import info.xiancloud.plugin.distribution.GroupProxy;
import info.xiancloud.plugin.distribution.UnitProxy;
import info.xiancloud.plugin.init.Destroyable;
import info.xiancloud.plugin.init.Initable;
import jdk.nashorn.internal.runtime.regexp.joni.constants.NodeStatus;

import java.util.List;

/**
 * @param <Instance>   eg. {@link UnitInstance UnitInstance}、{@link info.xiancloud.plugin.distribution.service_discovery.ApplicationInstance ApplicationInstance}
 * @param <Definition> eg. {@link UnitProxy UnitProxy}、{@link NodeStatus Node.Status}、{@link GroupProxy GroupProxy}
 * @author happyyangyuan
 */
public interface IDiscovery<Instance, Definition> extends Initable, Destroyable {

    /**
     * 返回目标实例之一，如果不可达，那么返回null；
     * 要求子类实现逻辑为：
     * 1、从缓存中获取服务实例
     * 2、缓存未命中，实时查询注册中心
     * 3、都未获取，返回null
     *
     * @param name 服务名
     * @return 如果不存在那么返回null
     */
    Instance lb(String name)/* throws AbstractXianException*/;

    /**
     * 要求子类实现逻辑为：
     * 1、从缓存中获取服务实例
     * 2、缓存未命中，实时查询注册中心
     * 3、都未获取，返回空列表
     *
     * @param name 服务名
     * @return 所有在线的实例，如果服务都不在线那么返回空集合，注意不是返回null。
     */
    List<Instance> all(String name)/* throws AbstractXianException*/;

    /**
     * 处于性能考虑，才提供本接口的；
     * 要求子类实现逻辑为：
     * 1、从缓存中获取服务实例
     * 2、缓存未命中，实时查询注册中心
     * 3、都未获取，返回null
     *
     * @param name 服务名
     * @return 返回在线的第一个实例，不做负载；如果服务不存在，那么返回null
     */
    Instance firstInstance(String name);

    /**
     * 要求子类实现逻辑为：
     * 1、从缓存中获取服务实例
     * 2、缓存未命中，实时查询注册中心
     * 3、都未获取，返回null
     *
     * @param id 实例id
     * @return id对应的实例，如果不存在id，那么返回null
     */
    Instance instance(String id);

    /**
     * 实时查询服务注册中心，获取服务名列表，在线的和历史的都能查到
     */
    List<String> queryForNames();

    /**
     * 要求子类实现逻辑为：
     * 1、从缓存中获取服务实例
     * 2、缓存未命中，实时查询注册中心 (暂未支持)
     * 3、都未获取，返回null
     *
     * @return 服务定义，如果服务不存在那么返回null
     */
    Definition newestDefinition(String name);

    /**
     * 向注册中心注册自己
     * register services of itself
     */
    void selfRegister();

    /**
     * 从注册中心把自己给注销掉
     * un register services of itself
     */
    void selfUnregister();

    /**
     * register a specified service instance.
     *
     * @param instance service instance to register
     */
    void register(Instance instance) throws Exception;

    /**
     * un register a specified service instance.
     *
     * @param instance the instance to un register.
     */
    void unregister(Instance instance) throws Exception;

}
