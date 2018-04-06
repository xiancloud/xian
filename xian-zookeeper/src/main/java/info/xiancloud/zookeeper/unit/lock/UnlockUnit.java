package info.xiancloud.zookeeper.unit.lock;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.zookeeper.unit.ZookeeperGroup;
import info.xiancloud.zookeeper.lock.ZkDistributedLock;

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
        return UnitMeta.createWithDescription("基于zookeeper的分布式解锁动作，解锁与加锁必须一一对应");
    }

    @Override
    public Input getInput() {
        return new Input().add("innerId", int.class, "被开启的那个锁的内部id", REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        ZkDistributedLock.unlock(msg.get("innerId", int.class));
        return UnitResponse.createSuccess("解锁成功:" + msg.get("innerId", int.class));
    }

    @Override
    public Group getGroup() {
        return ZookeeperGroup.singleton;
    }
}
