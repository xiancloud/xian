package info.xiancloud.discoverybridge.group;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.distribution.GroupProxy;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.distribution.NodeStatus;
import info.xiancloud.core.distribution.service_discovery.GroupDiscovery;
import info.xiancloud.core.distribution.service_discovery.GroupInstance;
import info.xiancloud.core.distribution.service_discovery.GroupInstanceIdBean;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.discoverybridge.DiscoveryBridgeGroup;

/**
 * @author happyyangyuan
 */
public class GroupRegistrationBridge implements Unit {
    @Override
    public Input getInput() {
        return Input.create()
                .add("group", Group.class, "", REQUIRED)
                .add("nodeStatus", NodeStatus.class, "", REQUIRED);
    }

    @Override
    public Group getGroup() {
        return DiscoveryBridgeGroup.singleton;
    }

    @Override
    public UnitResponse execute(UnitRequest request) {
        GroupProxy groupProxy = request.get("group", GroupProxy.class);
        NodeStatus nodeStatus = request.get("nodeStatus", NodeStatus.class);
        try {
            GroupDiscovery.singleton.register(groupInstance(groupProxy, nodeStatus));
            return UnitResponse.success();
        } catch (Exception e) {
            LOG.error(e);
            return UnitResponse.failure(e, "failed to register group");
        }
    }

    static GroupInstance groupInstance(GroupProxy groupProxy, NodeStatus nodeStatus) {
        GroupInstance groupInstance = new GroupInstance();
        groupInstance.setRegistrationTimestamp(nodeStatus.getInitTime());
        groupInstance.setPort(nodeStatus.getPort());
        groupInstance.setName(groupProxy.getName());
        groupInstance.setEnabled(true);
        groupInstance.setAddress(nodeStatus.getHost());
        groupInstance.setGroupInstanceIdBean(new GroupInstanceIdBean(groupProxy.getName(), LocalNodeManager.LOCAL_NODE_ID));
        groupInstance.setPayload(groupProxy);
        return groupInstance;
    }
}
