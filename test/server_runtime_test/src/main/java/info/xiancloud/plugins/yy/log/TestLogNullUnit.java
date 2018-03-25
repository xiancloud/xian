package info.xiancloud.plugins.yy.log;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.test.TestGroup;
import info.xiancloud.core.util.LOG;

/**
 * @author happyyangyuan
 */
public class TestLogNullUnit implements Unit {
    @Override
    public String getName() {
        return "testLogNull";
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        long start = System.nanoTime();
        while (System.nanoTime() - start < 2 * 1000000L) {
            LOG.info(null);
        }
        return UnitResponse.success();
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
