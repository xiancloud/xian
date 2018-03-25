package info.xiancloud.core.support.zk;

import info.xiancloud.core.Group;
import info.xiancloud.core.LocalUnitsManager;
import info.xiancloud.core.message.SyncXian;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;

import java.util.HashMap;

/**
 * LeaderElection
 *
 * @author happyyangyuan
 */
public class LeaderElection {

    //目前选举机制依赖zookeeper组件的存在
    public static boolean isLeader() {
        UnitResponse unitResponseObject = SyncXian.call("zookeeper", "isLeader", new HashMap());
        if (unitResponseObject.succeeded()) {
            return unitResponseObject.getData();
        } else {
            if (Group.CODE_UNIT_UNDEFINED.equals(unitResponseObject.getCode())) {
                LOG.info("当前节点并不主持leaderElection功能，这里认为当前节点认不是主节点");
            }
        }
        return false;
    }

    //目前选举机制依赖zookeeper组件的存在
    public static boolean leaderElectionEnabled() {
        return LocalUnitsManager.getLocalUnit("zookeeper", "isLeader") != null;
    }
}
