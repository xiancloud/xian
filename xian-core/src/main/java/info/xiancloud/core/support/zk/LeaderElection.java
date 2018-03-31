package info.xiancloud.core.support.zk;

import info.xiancloud.core.LocalUnitsManager;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.Xian;

import java.util.function.Consumer;

/**
 * LeaderElection
 *
 * @author happyyangyuan
 */
public class LeaderElection {

    //目前选举机制依赖zookeeper组件的存在
    public static void isLeader(Consumer<UnitResponse> consumer) {
        Xian.call("zookeeper", "isLeader", new NotifyHandler() {
            @Override
            protected void handle(UnitResponse unitResponse) {
                consumer.accept(unitResponse);
            }
        });
    }

    //目前选举机制依赖zookeeper组件的存在
    public static boolean leaderElectionEnabled() {
        return LocalUnitsManager.getLocalUnit("zookeeper", "isLeader") != null;
    }
}
