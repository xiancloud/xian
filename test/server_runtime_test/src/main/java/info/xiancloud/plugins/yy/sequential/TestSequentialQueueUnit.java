package info.xiancloud.plugins.yy.sequential;

import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.test.TestGroup;
import info.xiancloud.plugin.util.LOG;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author happyyangyuan
 */
public class TestSequentialQueueUnit implements Unit {
    //测试unit保序功能
    private static AtomicLong nano = new AtomicLong(Long.MIN_VALUE);

    @Override
    public String getName() {
        return "testSequentialQueue";
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("sequentialParam", String.class, "", REQUIRED, XHASH, SEQUENTIAL)
                .add("nano", long.class, "", REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            LOG.error(e);
        }
        if (nano.get() > msg.get("nano", long.class)) {
            LOG.error(new Throwable());
            return UnitResponse.failure(null, "保序算法有问题！");
        }
        nano.set(msg.get("nano", long.class));
        return UnitResponse.success();
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }

}
