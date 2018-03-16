package info.xiancloud.discoverybridge.group;

import info.xiancloud.discoverybridge.DiscoveryBridgeGroup;
import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.distribution.GroupProxy;
import info.xiancloud.plugin.distribution.LocalNodeManager;
import info.xiancloud.plugin.distribution.NodeStatus;
import info.xiancloud.plugin.distribution.service_discovery.GroupDiscovery;
import info.xiancloud.plugin.distribution.service_discovery.GroupInstance;
import info.xiancloud.plugin.distribution.service_discovery.GroupInstanceIdBean;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.util.LOG;

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
