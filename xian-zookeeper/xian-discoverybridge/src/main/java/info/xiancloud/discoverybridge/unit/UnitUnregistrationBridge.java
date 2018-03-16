package info.xiancloud.discoverybridge.unit;

import info.xiancloud.discoverybridge.DiscoveryBridgeGroup;
import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.distribution.NodeStatus;
import info.xiancloud.plugin.distribution.UnitProxy;
import info.xiancloud.plugin.distribution.service_discovery.UnitDiscovery;
import info.xiancloud.plugin.distribution.service_discovery.UnitInstance;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.util.LOG;

/**
 * 注销unit
 *
 * @author happyyangyuan
 */
public class UnitUnregistrationBridge implements Unit {
    @Override
    public Input getInput() {
        return Input.create()
                .add("unit", Unit.class, "待注册的unit", REQUIRED)
                .add("nodeStatus", NodeStatus.class, "节点装填", REQUIRED);
    }

    @Override
    public Group getGroup() {
        return DiscoveryBridgeGroup.singleton;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        UnitProxy unitProxy = msg.get("unit", UnitProxy.class);
        NodeStatus nodeStatus = msg.get("nodeStatus", NodeStatus.class);
        UnitInstance unitInstance = UnitRegistrationBridge.unitInstance(unitProxy, nodeStatus);
        try {
            UnitDiscovery.singleton.unregister(unitInstance);
            return UnitResponse.success();
        } catch (Exception e) {
            LOG.error(e);
            return UnitResponse.failure(e, "unit un registration failure.");
        }
    }
}
