package info.xiancloud.core.support.zk;

import info.xiancloud.core.LocalUnitsManager;
import info.xiancloud.core.message.SingleRxXian;
import io.reactivex.Single;

import java.util.Objects;

/**
 * LeaderElection
 *
 * @author happyyangyuan
 */
public class LeaderElection {

    //目前选举机制依赖zookeeper组件的存在
    public static Single<Boolean> isLeader() {
        return SingleRxXian
                .call("zookeeper", "isLeader")
                .flatMap(unitResponse -> Single.just(Objects.requireNonNull(unitResponse.dataToBoolean())));
    }

    //目前选举机制依赖zookeeper组件的存在
    public static boolean leaderElectionEnabled() {
        return LocalUnitsManager.getLocalUnit("zookeeper", "isLeader") != null;
    }
}
