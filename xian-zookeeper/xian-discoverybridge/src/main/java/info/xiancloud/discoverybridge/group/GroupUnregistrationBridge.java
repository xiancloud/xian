package info.xiancloud.discoverybridge.group;

import info.xiancloud.discoverybridge.DiscoveryBridgeGroup;
import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.distribution.GroupProxy;
import info.xiancloud.plugin.distribution.NodeStatus;
import info.xiancloud.plugin.distribution.service_discovery.GroupDiscovery;
import info.xiancloud.plugin.distribution.service_discovery.GroupInstance;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;

/**
 * @author happyyangyuan
 */
public class GroupUnregistrationBridge implements Unit {
    @Override
    public Input getInput() {
        return Input.create()
                .add("group", Group.class, "group object", REQUIRED)
                .add("nodeStatus", NodeStatus.class, "node status", REQUIRED);
    }

    @Override
    public Group getGroup() {
        return DiscoveryBridgeGroup.singleton;
    }

    @Override
    public UnitResponse execute(UnitRequest request) {
        GroupProxy groupProxy = request.get("group", GroupProxy.class);
        NodeStatus nodeStatus = request.get("nodeStatus", NodeStatus.class);
        GroupInstance groupInstance = GroupRegistrationBridge.groupInstance(groupProxy, nodeStatus);
        try {
            GroupDiscovery.singleton.unregister(groupInstance);
            return UnitResponse.success();
        } catch (Exception e) {
            return UnitResponse.failure(e, "group注销失败.");
        }
    }
}
