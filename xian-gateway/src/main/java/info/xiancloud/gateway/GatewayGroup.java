package info.xiancloud.gateway;

import info.xiancloud.core.Group;

/**
 * @author happyyangyuan
 */
public class GatewayGroup implements Group {

    public static final Group singleton = new GatewayGroup();

    @Override
    public String getDescription() {
        return "业务流程调度组件";
    }

    @Override
    public String getName() {
        return "gateway";
    }

}
