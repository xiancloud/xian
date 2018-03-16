package info.xiancloud.discoverybridge.application;

import info.xiancloud.discoverybridge.DiscoveryBridgeGroup;
import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.UnitMeta;
import info.xiancloud.plugin.distribution.NodeStatus;
import info.xiancloud.plugin.distribution.service_discovery.ApplicationDiscovery;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;

/**
 * @author happyyangyuan
 */
public class ApplicationUnregistrationBridge implements Unit {
    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("注销某个节点");
    }

    @Override
    public Input getInput() {
        return Input.create()
                .add("nodeStatus", NodeStatus.class, "node status object", REQUIRED);
    }

    @Override
    public Group getGroup() {
        return DiscoveryBridgeGroup.singleton;
    }

    @Override
    public UnitResponse execute(UnitRequest request) {
        NodeStatus nodeStatus = request.get("nodeStatus", NodeStatus.class);
        try {
            ApplicationDiscovery.singleton.unregister(ApplicationRegistrationBridge.applicationInstance(nodeStatus));
            return UnitResponse.success();
        } catch (Exception e) {
            return UnitResponse.exception(e);
        }
    }
}
