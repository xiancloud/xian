package info.xiancloud.plugin.unit.leader_elect;

import info.xiancloud.plugin.*;
import info.xiancloud.plugin.unit.ZookeeperGroup;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.zookeeper.leader.ZkLeaderElection;

/**
 * @author happyyangyuan
 */
public class IsLeaderUnit implements Unit {
    @Override
    public String getName() {
        return "isLeader";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("判断自身是否是leader");
    }

    @Override
    public Input getInput() {
        return new Input();
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        return UnitResponse.success(ZkLeaderElection.isLeader());
    }

    @Override
    public Group getGroup() {
        return ZookeeperGroup.singleton;
    }
}
