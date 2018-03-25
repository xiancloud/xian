package info.xiancloud.zookeeper.service_discovery_new;

import info.xiancloud.core.LocalUnitsManager;
import info.xiancloud.core.Unit;
import info.xiancloud.core.distribution.*;
import info.xiancloud.core.distribution.service_discovery.*;
import info.xiancloud.core.util.EnvUtil;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceType;

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

    public static ServiceInstance<NodeStatus> curatorServiceInstance(ApplicationInstance applicationInstance) {
        try {
            return ServiceInstance.<NodeStatus>builder()
                    .address(/*applicationInstance.getPayload().getHost()*/applicationInstance.getAddress())
                    .enabled(true)
                    .id(applicationInstance.getId())
                    .name(applicationInstance.getName())
                    .port(applicationInstance.getPort())
                    .payload(applicationInstance.getPayload())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ServiceInstance<GroupProxy> curatorServiceInstance(GroupInstance groupInstance) {
        try {
            return ServiceInstance.<GroupProxy>builder()
                    .address(groupInstance.getAddress())
                    .enabled(true)
                    .id(groupInstance.getId())
                    .name(groupInstance.getName())
                    .port(groupInstance.getPort())
                    .payload(groupInstance.getPayload())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ServiceInstance<UnitProxy> curatorServiceInstance(UnitInstance unitInstance) {
        try {
            return ServiceInstance.<UnitProxy>builder()
                    .address(unitInstance.getAddress())
                    .enabled(true)
                    .id(unitInstance.getId())
                    .name(unitInstance.getName())
                    .port(unitInstance.getPort())
                    .payload(unitInstance.getPayload())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ServiceInstance<NodeStatus> thisCuratorServiceInstance() throws Exception {
        return ServiceInstance.<NodeStatus>builder()
                .address(EnvUtil.getLocalIp())
                .enabled(true)
                .id(LocalNodeManager.LOCAL_NODE_ID)
                .name(EnvUtil.getApplication())
                .port(Node.RPC_PORT)
                .payload(LocalNodeManager.singleton.getFullStatus())
                .build();
    }

    public static ServiceInstance<UnitProxy> thisCuratorServiceInstance(Unit unit) throws Exception {
        String fullUnitName = Unit.fullName(unit);
        return ServiceInstance.<UnitProxy>builder()
                .address(EnvUtil.getLocalIp())
                .enabled(true)
                .id(new UnitInstanceIdBean(fullUnitName, LocalNodeManager.LOCAL_NODE_ID).getUnitInstanceId())
                .name(fullUnitName)
                .port(Node.RPC_PORT)
                .payload(UnitProxy.create(unit))
                .serviceType(ServiceType.DYNAMIC)
                .build();
    }

    public static ServiceInstance<GroupProxy> thisCuratorServiceInstance(String groupName) throws Exception {
        return ServiceInstance.<GroupProxy>builder()
                .address(EnvUtil.getLocalIp())
                .enabled(true)
                .id(new GroupInstanceIdBean(groupName, LocalNodeManager.LOCAL_NODE_ID).getGroupInstanceId())
                .name(groupName)
                .port(Node.RPC_PORT)
                .payload(GroupProxy.create(LocalUnitsManager.getGroupByName(groupName)))
                .build();
    }

}
