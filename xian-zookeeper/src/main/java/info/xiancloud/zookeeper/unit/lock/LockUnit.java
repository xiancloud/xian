package info.xiancloud.zookeeper.unit.lock;

import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.zookeeper.lock.ZkDistributedLock;
import info.xiancloud.zookeeper.unit.ZookeeperGroup;

import java.util.concurrent.TimeoutException;

/**
 * @author happyyangyuan
 * @deprecated zookeeper distributed lock is proofed to be poor performance.
 */
public class LockUnit implements Unit {
    @Override
    public String getName() {
        return "lock";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("加锁");
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("timeoutInMilli", long.class, "超时时间，单位毫秒，超时后，任务不会被执行", REQUIRED)
                .add("lockId", String.class, "锁的id，注意，必须是全局唯一的", REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        try {
            int innerId = ZkDistributedLock.lock(msg.get("lockId", String.class), msg.get("timeoutInMilli", long.class));
            handler.handle(UnitResponse.createSuccess(innerId));
        } catch (TimeoutException e) {
            handler.handle(UnitResponse.create(Group.CODE_TIME_OUT, msg.get("lockId"), "获取锁超时:" + msg.get("lockId", String.class)));
        }
    }

    @Override
    public Group getGroup() {
        return ZookeeperGroup.singleton;
    }
}
