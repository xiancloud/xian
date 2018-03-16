package info.xiancloud.discoverybridge.unit;

import info.xiancloud.discoverybridge.DiscoveryBridgeGroup;
import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.distribution.NodeStatus;
import info.xiancloud.plugin.distribution.UnitProxy;
import info.xiancloud.plugin.distribution.service_discovery.UnitDiscovery;
import info.xiancloud.plugin.distribution.service_discovery.UnitInstance;
import info.xiancloud.plugin.distribution.service_discovery.UnitInstanceIdBean;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.util.LOG;

/**
 * @author happyyangyuan
 */
public class UnitRegistrationBridge implements Unit {
    @Override
    public String getName() {
        return "unitRegistrationBridge";
    }

    @Override
    public Input getInput() {
        return Input.create().add("unit", Unit.class, "the unit u want to register.", REQUIRED)
                .add("nodeStatus", NodeStatus.class, "the node status bean", REQUIRED);
    }

    @Override
    public Group getGroup() {
        return DiscoveryBridgeGroup.singleton;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        UnitProxy unitProxy = msg.get("unit", UnitProxy.class);
        NodeStatus nodeStatus = msg.get("nodeStatus", NodeStatus.class);
        UnitInstance unitInstance = unitInstance(unitProxy, nodeStatus);
        try {
            UnitDiscovery.singleton.register(unitInstance);
            return UnitResponse.success();
        } catch (Exception e) {
            LOG.error(e);
            return UnitResponse.failure(null, "registration failed.");
        }
    }

    static UnitInstance unitInstance(UnitProxy unitProxy, NodeStatus nodeStatus) {
        UnitInstance unitInstance = new UnitInstance();
        unitInstance.setPayload(UnitProxy.create(unitProxy));
        unitInstance.setRegistrationTimestamp(nodeStatus.getInitTime());
        unitInstance.setPort(nodeStatus.getPort());
        unitInstance.setName(Unit.fullName(unitProxy));
        unitInstance.setEnabled(true);
        unitInstance.setAddress(nodeStatus.getHost());
        unitInstance.setUnitInstanceIdBean(new UnitInstanceIdBean(Unit.fullName(unitProxy), nodeStatus.getNodeId()));
        return unitInstance;
    }
}
