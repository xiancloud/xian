package info.xiancloud.plugin.distribution.loadbalance;

import info.xiancloud.plugin.distribution.exception.AbstractXianException;

import java.util.List;

/**
 * @param <Instance>   服务实例
 * @param <Definition> 服务定义
 * @author happyyangyuan
 */
public interface IRouter<Instance, Definition> {
    /**
     * @param name 服务名
     * @return 负载均衡后的实例；注意，虽然是负载实例，但是优先返回本地实例
     * @throws AbstractXianException xian通用异常，子类需要重写为具体的异常
     */
    Instance loadBalancedInstance(String name) throws AbstractXianException;

    /**
     * 出于性能考虑，提供本接口方法来实现一系列检查
     *
     * @param name 服务名
     * @return 第一个在线的实例
     * @throws AbstractXianException xian通用异常，子类需要重写为具体的异常
     */
    Instance firstInstance(String name) throws AbstractXianException;

    /**
     * 获取本地实例
     *
     * @param name 服务名
     * @return 如果本地有该服务定义，那么返回对应的服务实例，否则返回null
     */
    Instance localInstance(String name);

    /**
     * @param name 服务名
     * @return 所有在线的实例列表
     * @throws AbstractXianException xian通用异常，子类需要重写为具体的异常
     */
    List<Instance> allInstances(String name) throws AbstractXianException;

    /**
     * @param name 服务名
     * @return 优先本地的定义 or 最新注册的服务定义
     * @throws AbstractXianException xian通用异常，子类需要重写为具体的异常
     */
    Definition newestDefinition(String name) throws AbstractXianException;

    /**
     * @param instanceId 实例id
     * @return 服务实例
     * @throws AbstractXianException xian通用异常，子类需要重写为具体的异常
     */
    Instance getInstance(String instanceId) throws AbstractXianException;
}
