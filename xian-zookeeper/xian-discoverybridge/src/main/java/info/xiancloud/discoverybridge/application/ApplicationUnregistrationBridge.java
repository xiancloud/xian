package info.xiancloud.discoverybridge.application;

import info.xiancloud.core.*;
import info.xiancloud.core.distribution.NodeStatus;
import info.xiancloud.core.distribution.service_discovery.ApplicationDiscovery;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.discoverybridge.DiscoveryBridgeGroup;

/**
 * @author happyyangyuan
 */
@SuppressWarnings("all")
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
    public void execute(UnitRequest request, Handler<UnitResponse> handler) {
        NodeStatus nodeStatus = request.get("nodeStatus", NodeStatus.class);
        try {
            ApplicationDiscovery.singleton.unregister(ApplicationRegistrationBridge.applicationInstance(nodeStatus));
            handler.handle(UnitResponse.createSuccess());
            return;
        } catch (Exception e) {
            LOG.error(e);
            handler.handle(UnitResponse.createException(e));
            return;
        }
    }
}
