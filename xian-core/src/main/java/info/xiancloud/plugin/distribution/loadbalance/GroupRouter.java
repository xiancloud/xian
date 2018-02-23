package info.xiancloud.plugin.distribution.loadbalance;

import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.LocalUnitsManager;
import info.xiancloud.plugin.distribution.GroupProxy;
import info.xiancloud.plugin.distribution.LocalNodeManager;
import info.xiancloud.plugin.distribution.Node;
import info.xiancloud.plugin.distribution.exception.GroupInstanceOfflineException;
import info.xiancloud.plugin.distribution.exception.GroupOfflineException;
import info.xiancloud.plugin.distribution.exception.GroupUndefinedException;
import info.xiancloud.plugin.distribution.service_discovery.GroupDiscovery;
import info.xiancloud.plugin.distribution.service_discovery.GroupInstance;
import info.xiancloud.plugin.distribution.service_discovery.GroupInstanceIdBean;
import info.xiancloud.plugin.util.EnvUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * group router
 *
 * @author happyyangyuan
 */
public class GroupRouter implements IRouter<GroupInstance, GroupProxy> {

    public static final GroupRouter singleton = new GroupRouter();

    private GroupRouter() {
    }

    public GroupInstance loadBalancedInstance(String groupName) throws GroupOfflineException, GroupUndefinedException {
        GroupInstance instance = localInstance(groupName);
        if (instance != null) return instance;
        newestDefinition(groupName);
        if (GroupDiscovery.singleton != null)
            instance = GroupDiscovery.singleton.lb(groupName);
        if (instance != null) return instance;
        throw new GroupOfflineException(groupName);
    }

    @Override
    public GroupInstance firstInstance(String groupName) throws GroupOfflineException, GroupUndefinedException {
        GroupInstance instance = localInstance(groupName);
        if (instance != null) return instance;
        newestDefinition(groupName);
        if (GroupDiscovery.singleton != null)
            instance = GroupDiscovery.singleton.firstInstance(groupName);
        if (instance != null) return instance;
        throw new GroupOfflineException(groupName);
    }

    @Override
    public GroupInstance localInstance(String groupName) {
        return LocalUnitsManager.unitMap(unitMap -> {
            if (unitMap.containsKey(groupName)) {
                GroupInstance serviceInstance = new GroupInstance();
                serviceInstance.setRegistrationTimestamp(LocalNodeManager.singleton.getFullStatus().getInitTime());
                serviceInstance.setPort(Node.RPC_PORT);
                serviceInstance.setName(groupName);
                serviceInstance.setEnabled(true);
                serviceInstance.setAddress(EnvUtil.getLocalIp());
                serviceInstance.setId(new GroupInstanceIdBean(groupName, LocalNodeManager.LOCAL_NODE_ID).getGroupInstanceId());
                serviceInstance.setPayload(GroupProxy.create(LocalUnitsManager.getGroupByName(groupName)));
                return serviceInstance;
            } else {
                return null;
            }
        });
    }

    public List<GroupInstance> allInstances(String groupName) throws GroupOfflineException, GroupUndefinedException {
        newestDefinition(groupName);
        List<GroupInstance> instances;
        if (GroupDiscovery.singleton != null)
            instances = GroupDiscovery.singleton.all(groupName);
        else {
            instances = new ArrayList<>();
            instances.add(localInstance(groupName));
        }
        if (instances.isEmpty()) throw new GroupOfflineException(groupName);
        /*instances.sort(Comparator.comparing(GroupInstance::getNodeId));
         we do not sort any more, sort by your self if you need consistent hash.
         */
        return instances;
    }


    public GroupProxy newestDefinition(String groupName) throws GroupUndefinedException {
        Group localGroup = LocalUnitsManager.getGroupByName(groupName);
        if (localGroup != null) return GroupProxy.create(localGroup);
        if (GroupDiscovery.singleton != null) {
            GroupProxy groupProxy = GroupDiscovery.singleton.newestDefinition(groupName);
            if (groupProxy != null) return groupProxy;
        }
        throw new GroupUndefinedException(groupName);
    }

    @Override
    public GroupInstance getInstance(String instanceId) throws GroupInstanceOfflineException {
        if (GroupDiscovery.singleton == null)
            throw new RuntimeException("Service discovery is disabled! No service discovery plugin is provided.");
        GroupInstance instance = GroupDiscovery.singleton.instance(instanceId);
        if (instance == null)
            throw new GroupInstanceOfflineException(instanceId);
        return instance;
    }


}
