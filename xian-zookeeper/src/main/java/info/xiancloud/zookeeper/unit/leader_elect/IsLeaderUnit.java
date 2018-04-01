package info.xiancloud.zookeeper.unit.leader_elect;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.zookeeper.unit.ZookeeperGroup;
import info.xiancloud.zookeeper.leader.ZkLeaderElection;

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
        return UnitResponse.createSuccess(ZkLeaderElection.isLeader());
    }

    @Override
    public Group getGroup() {
        return ZookeeperGroup.singleton;
    }
}
