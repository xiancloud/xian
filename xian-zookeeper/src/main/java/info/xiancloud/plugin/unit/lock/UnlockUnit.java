package info.xiancloud.plugin.unit.lock;

import info.xiancloud.plugin.*;
import info.xiancloud.plugin.unit.ZookeeperGroup;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.zookeeper.lock.ZkDistributedLock;

/**
 * @author happyyangyuan
 * @deprecated zookeeper lock is proved to be under poor performance.
 */
public class UnlockUnit implements Unit {
    @Override
    public String getName() {
        return "unlock";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("基于zookeeper的分布式解锁动作，解锁与加锁必须一一对应");
    }

    @Override
    public Input getInput() {
        return new Input().add("innerId", int.class, "被开启的那个锁的内部id", REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        ZkDistributedLock.unlock(msg.get("innerId", int.class));
        return UnitResponse.success("解锁成功:" + msg.get("innerId", int.class));
    }

    @Override
    public Group getGroup() {
        return ZookeeperGroup.singleton;
    }
}
