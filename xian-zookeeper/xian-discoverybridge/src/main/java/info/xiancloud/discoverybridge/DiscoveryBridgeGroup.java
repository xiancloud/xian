package info.xiancloud.discoverybridge;

import info.xiancloud.core.Group;

/**
 * @author happyyangyuan
 */
public class DiscoveryBridgeGroup implements Group {
    @Override
    public String getName() {
        return "discoveryBridge";
    }

    @Override
    public String getDescription() {
        return "discovery bridge.";
    }

    public static final DiscoveryBridgeGroup singleton = new DiscoveryBridgeGroup();
}
