package info.xiancloud.zookeeper.unit.leader_elect;

import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.zookeeper.leader.ZkLeaderElection;
import info.xiancloud.zookeeper.unit.ZookeeperGroup;

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
        return UnitMeta.createWithDescription("判断自身是否是leader")
                .setDocApi(false);
    }

    @Override
    public Input getInput() {
        return new Input();
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        handler.handle(UnitResponse.createSuccess(ZkLeaderElection.isLeader()));
    }

    @Override
    public Group getGroup() {
        return ZookeeperGroup.singleton;
    }
}
