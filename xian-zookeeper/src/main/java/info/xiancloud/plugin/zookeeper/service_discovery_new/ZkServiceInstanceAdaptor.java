package info.xiancloud.plugin.zookeeper.service_discovery_new;

import info.xiancloud.plugin.distribution.GroupProxy;
import info.xiancloud.plugin.distribution.NodeStatus;
import info.xiancloud.plugin.distribution.UnitProxy;
import info.xiancloud.plugin.distribution.service_discovery.ApplicationInstance;
import info.xiancloud.plugin.distribution.service_discovery.GroupInstance;
import info.xiancloud.plugin.distribution.service_discovery.UnitInstance;
import org.apache.curator.x.discovery.ServiceInstance;

/**
 * zk插件和xianframe的服务注册适配
 *
 * @author happyyangyuan
 */
public class ZkServiceInstanceAdaptor {

    public static ApplicationInstance applicationInstance(ServiceInstance<NodeStatus> serviceInstance) {
        if (serviceInstance == null) return null;
        ApplicationInstance applicationInstance = new ApplicationInstance();
        applicationInstance.setAddress(serviceInstance.getAddress());
        applicationInstance.setEnabled(serviceInstance.isEnabled());
        applicationInstance.setId(serviceInstance.getId());
        applicationInstance.setName(serviceInstance.getName());
        applicationInstance.setPayload(serviceInstance.getPayload());
        applicationInstance.setPort(serviceInstance.getPort());
        applicationInstance.setRegistrationTimestamp(serviceInstance.getRegistrationTimeUTC());
        return applicationInstance;
    }

    public static UnitInstance unitInstance(ServiceInstance<UnitProxy> serviceInstance) {
        if (serviceInstance == null) return null;
        UnitInstance unitInstance = new UnitInstance();
        unitInstance.setId(serviceInstance.getId());
        unitInstance.setAddress(serviceInstance.getAddress());
        unitInstance.setEnabled(serviceInstance.isEnabled());
        unitInstance.setName(serviceInstance.getName());
        unitInstance.setPayload(serviceInstance.getPayload());
        unitInstance.setPort(serviceInstance.getPort());
        unitInstance.setRegistrationTimestamp(serviceInstance.getRegistrationTimeUTC());
        return unitInstance;
    }

    public static GroupInstance groupInstance(ServiceInstance<GroupProxy> curatorServiceInstance) {
        if (curatorServiceInstance == null) return null;
        GroupInstance groupInstance = new GroupInstance();
        groupInstance.setId(curatorServiceInstance.getId());
        groupInstance.setAddress(curatorServiceInstance.getAddress());
        groupInstance.setEnabled(curatorServiceInstance.isEnabled());
        groupInstance.setName(curatorServiceInstance.getName());
        groupInstance.setPayload(curatorServiceInstance.getPayload());
        groupInstance.setPort(curatorServiceInstance.getPort());
        groupInstance.setRegistrationTimestamp(curatorServiceInstance.getRegistrationTimeUTC());
        return groupInstance;
    }
}
