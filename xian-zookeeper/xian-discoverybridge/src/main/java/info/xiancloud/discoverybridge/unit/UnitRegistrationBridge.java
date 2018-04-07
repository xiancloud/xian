package info.xiancloud.discoverybridge.unit;

import info.xiancloud.core.Group;
import info.xiancloud.core.Handler;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.distribution.NodeStatus;
import info.xiancloud.core.distribution.UnitProxy;
import info.xiancloud.core.distribution.service_discovery.UnitDiscovery;
import info.xiancloud.core.distribution.service_discovery.UnitInstance;
import info.xiancloud.core.distribution.service_discovery.UnitInstanceIdBean;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.discoverybridge.DiscoveryBridgeGroup;

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
        return Input.create().add("unit", Unit.class, "the unit you want to register.", REQUIRED)
                .add("nodeStatus", NodeStatus.class, "the node status bean", REQUIRED);
    }

    @Override
    public Group getGroup() {
        return DiscoveryBridgeGroup.singleton;
    }

    @Override
    @SuppressWarnings("all")
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        UnitProxy unitProxy = msg.get("unit", UnitProxy.class);
        NodeStatus nodeStatus = msg.get("nodeStatus", NodeStatus.class);
        UnitInstance unitInstance = unitInstance(unitProxy, nodeStatus);
        try {
            UnitDiscovery.singleton.register(unitInstance);//Warning for blocking method!
            handler.handle(UnitResponse.createSuccess());
            return;
        } catch (Exception e) {
            LOG.error(e);
            handler.handle(UnitResponse.createUnknownError(e, "registration failed."));
            return;
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
