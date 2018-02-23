package info.xiancloud.plugin.distribution.loadbalance;

import info.xiancloud.plugin.distribution.LocalNodeManager;
import info.xiancloud.plugin.distribution.Node;
import info.xiancloud.plugin.distribution.NodeStatus;
import info.xiancloud.plugin.distribution.exception.ApplicationInstanceOfflineException;
import info.xiancloud.plugin.distribution.exception.ApplicationOfflineException;
import info.xiancloud.plugin.distribution.exception.ApplicationUndefinedException;
import info.xiancloud.plugin.distribution.service_discovery.ApplicationDiscovery;
import info.xiancloud.plugin.distribution.service_discovery.ApplicationInstance;
import info.xiancloud.plugin.util.EnvUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 根据application寻址，兼任其他寻址
 * 特别说明：Router不应当具备消息堆积能力，即它不做"目标application不在线即转发至中转节点形成堆积的能力"
 *
 * @author happyyangyuan
 */
public class ApplicationRouter implements IRouter<ApplicationInstance, NodeStatus> {

    public static final ApplicationRouter singleton = new ApplicationRouter();

    private ApplicationRouter() {
    }

    public ApplicationInstance loadBalancedInstance(String application) throws ApplicationOfflineException, ApplicationUndefinedException {
        ApplicationInstance instance = localInstance(application);
        if (instance != null) return instance;
        newestDefinition(application);
        if (ApplicationDiscovery.singleton != null)
            instance = ApplicationDiscovery.singleton.lb(application);
        if (instance != null) return instance;
        throw new ApplicationOfflineException(application);
    }

    @Override
    public ApplicationInstance firstInstance(String application) throws ApplicationOfflineException, ApplicationUndefinedException {
        ApplicationInstance instance = localInstance(application);
        if (instance != null) return instance;
        newestDefinition(application);
        if (ApplicationDiscovery.singleton != null)
            instance = ApplicationDiscovery.singleton.firstInstance(application);
        if (instance != null) return instance;
        throw new ApplicationOfflineException(application);
    }

    @Override
    public ApplicationInstance localInstance(String application) {
        if (EnvUtil.getApplication().equals(application)) {
            //本地节点直接返回不需要从服务注册器内寻找 todo 请封装成统一代码，然后使用adapter与curator serviceInstance互转
            ApplicationInstance applicationInstance = new ApplicationInstance();
            applicationInstance.setRegistrationTimestamp(LocalNodeManager.singleton.getFullStatus().getInitTime());
            applicationInstance.setPort(Node.RPC_PORT);
            applicationInstance.setPayload(LocalNodeManager.singleton.getFullStatus());
            applicationInstance.setName(application);
            applicationInstance.setId(LocalNodeManager.LOCAL_NODE_ID);
            applicationInstance.setEnabled(true);
            applicationInstance.setAddress(EnvUtil.getLocalIp());
            return applicationInstance;
        }
        return null;
    }

    /**
     * @return 根据目标获取相应的客户端列表, 目前本方法主要提供一致性哈希节点id列表入参；
     */
    public List<ApplicationInstance> allInstances(String application) throws ApplicationOfflineException, ApplicationUndefinedException {
        newestDefinition(application);
        List<ApplicationInstance> instances;
        if (ApplicationDiscovery.singleton != null)
            instances = ApplicationDiscovery.singleton.all(application);
        else {
            instances = new ArrayList<>();
            instances.add(localInstance(application));
        }
        if (instances.isEmpty()) throw new ApplicationOfflineException(application);
        /*instances.sort(Comparator.comparing(ApplicationInstance::getNodeId));
        we do not sort this any more, if you want to use the consistent hash, sort by yourself.*/
        return instances;
    }

    /**
     * @return 检查application是否在线
     */
    public boolean available(String application) {
        try {
            firstInstance(application);
            return true;
        } catch (ApplicationOfflineException | ApplicationUndefinedException ignored) {
            return false;
        }
    }

    /**
     * 获取指定id的节点；本方法扫描本地内存缓存数据，支持适当高频访问.
     */
    public ApplicationInstance getInstance(String nodeId) throws ApplicationInstanceOfflineException {
        if (ApplicationDiscovery.singleton == null)
            throw new RuntimeException("Service discovery is disabled! No service discovery plugin is provided.");
        ApplicationInstance instance = ApplicationDiscovery.singleton.instance(nodeId);
        if (instance == null)
            throw new ApplicationInstanceOfflineException(nodeId);
        return instance;
    }

    /**
     * 获取application定义
     *
     * @param application application名称
     * @throws ApplicationUndefinedException application未定义
     */
    public NodeStatus newestDefinition(String application) throws ApplicationUndefinedException {
        if (Objects.equals(application, EnvUtil.getApplication()))
            return LocalNodeManager.singleton.getFullStatus();
        if (ApplicationDiscovery.singleton != null) {
            NodeStatus status = ApplicationDiscovery.singleton.newestDefinition(application);
            if (status != null) return status;
        }
        throw new ApplicationUndefinedException(application);
    }

}
