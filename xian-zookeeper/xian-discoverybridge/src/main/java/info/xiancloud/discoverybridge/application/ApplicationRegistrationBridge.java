package info.xiancloud.discoverybridge.application;

import info.xiancloud.discoverybridge.DiscoveryBridgeGroup;
import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.distribution.NodeStatus;
import info.xiancloud.plugin.distribution.service_discovery.ApplicationDiscovery;
import info.xiancloud.plugin.distribution.service_discovery.ApplicationInstance;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.id.NodeIdBean;
import info.xiancloud.plugin.util.LOG;

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
            return UnitResponse.success();
        } catch (Exception e) {
            LOG.error(e);
            return UnitResponse.failure(e, "注册application失败");
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
