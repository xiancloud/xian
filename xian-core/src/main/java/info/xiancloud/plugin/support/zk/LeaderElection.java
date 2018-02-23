package info.xiancloud.plugin.support.zk;

import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.LocalUnitsManager;
import info.xiancloud.plugin.message.SyncXian;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.util.LOG;

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
