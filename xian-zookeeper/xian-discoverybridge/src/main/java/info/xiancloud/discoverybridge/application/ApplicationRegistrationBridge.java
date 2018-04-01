package info.xiancloud.discoverybridge.application;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.distribution.NodeStatus;
import info.xiancloud.core.distribution.service_discovery.ApplicationDiscovery;
import info.xiancloud.core.distribution.service_discovery.ApplicationInstance;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.id.NodeIdBean;
import info.xiancloud.core.util.LOG;
import info.xiancloud.discoverybridge.DiscoveryBridgeGroup;

/**
 * @author happyyangyuan
 */
public class ApplicationRegistrationBridge implements Unit {
    @Override
    public Input getInput() {
        return Input.create().add("nodeStatus", NodeStatus.class, "node status", REQUIRED);
    }

    @Override
    public Group getGroup() {
        return DiscoveryBridgeGroup.singleton;
    }

    @Override
    public UnitResponse execute(UnitRequest request) {
        NodeStatus nodeStatus = request.get("nodeStatus", NodeStatus.class);
        try {
            ApplicationDiscovery.singleton.register(applicationInstance(nodeStatus));
            return UnitResponse.createSuccess();
        } catch (Exception e) {
            LOG.error(e);
            return UnitResponse.createUnknownError(e, "注册application失败");
        }
    }

    static ApplicationInstance applicationInstance(NodeStatus nodeStatus) {
        ApplicationInstance applicationInstance = new ApplicationInstance();
        applicationInstance.setRegistrationTimestamp(nodeStatus.getInitTime());
        applicationInstance.setPort(nodeStatus.getPort());
        applicationInstance.setPayload(nodeStatus);
        applicationInstance.setName(NodeIdBean.parse(nodeStatus.getNodeId()).getApplication());
        applicationInstance.setId(nodeStatus.getNodeId());
        applicationInstance.setEnabled(true);
        applicationInstance.setAddress(nodeStatus.getHost());
        return applicationInstance;
    }
}
