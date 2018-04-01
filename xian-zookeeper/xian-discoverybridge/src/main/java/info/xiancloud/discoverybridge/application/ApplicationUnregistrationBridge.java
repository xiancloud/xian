package info.xiancloud.discoverybridge.application;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.distribution.NodeStatus;
import info.xiancloud.core.distribution.service_discovery.ApplicationDiscovery;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.discoverybridge.DiscoveryBridgeGroup;

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
            return UnitResponse.createSuccess();
        } catch (Exception e) {
            return UnitResponse.createException(e);
        }
    }
}
