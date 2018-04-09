package info.xiancloud.zookeeper.unit.lock;

import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.zk.DistZkLocker;
import info.xiancloud.zookeeper.lock.ZkDistributedLock;
import info.xiancloud.zookeeper.unit.ZookeeperGroup;

/**
 * @author happyyangyuan
 * @deprecated zookeeper distributed lock is proved to be poor performance.
 */
public class LockUnit implements Unit {
    @Override
    public String getName() {
        return "lock";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("加锁").setDocApi(false)
                .setSuccessfulUnitResponse(UnitResponse.createSuccess("获取锁成功则该值为锁的内部id，int类型"))
                .addFailedUnitResponse(UnitResponse.createError(Group.CODE_TIME_OUT, "获取锁超时则该值为-1", "获取锁超时: {lockId}"));
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("timeoutInMilli", long.class, "超时时间，单位毫秒，超时后，任务不会被执行", REQUIRED)
                .add("lockId", String.class, "锁的id，注意，必须是全局唯一的", REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        int innerId = ZkDistributedLock.lock(msg.get("lockId", String.class), msg.get("timeoutInMilli", long.class));
        if (innerId == DistZkLocker.TIME_OUT_INNER_ID) {
            handler.handle(UnitResponse.create(Group.CODE_TIME_OUT, innerId, "获取锁超时:" + msg.get("lockId", String.class)));
        } else {
            handler.handle(UnitResponse.createSuccess(innerId));
        }
    }

    @Override
    public Group getGroup() {
        return ZookeeperGroup.singleton;
    }
}
