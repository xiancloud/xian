package info.xiancloud.plugin.unit.lock;

import info.xiancloud.plugin.*;
import info.xiancloud.plugin.unit.ZookeeperGroup;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.zookeeper.lock.ZkDistributedLock;

import java.util.concurrent.TimeoutException;

/**
 * @author happyyangyuan
 * @deprecated zookeeper distributed lock is under poor perfance.
 */
public class LockUnit implements Unit {
    @Override
    public String getName() {
        return "lock";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("加锁");
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("timeoutInMilli", long.class, "超时时间，单位毫秒，超时后，任务不会被执行", REQUIRED)
                .add("lockId", String.class, "锁的id，注意，必须是全局唯一的", REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        try {
            int innerId = ZkDistributedLock.lock(msg.get("lockId", String.class), msg.get("timeoutInMilli", long.class));
            return UnitResponse.success(innerId);
        } catch (TimeoutException e) {
            return UnitResponse.create(Group.CODE_TIME_OUT, msg.get("lockId"), "获取锁超时:" + msg.get("lockId", String.class));
        }
    }

    @Override
    public Group getGroup() {
        return ZookeeperGroup.singleton;
    }
}
