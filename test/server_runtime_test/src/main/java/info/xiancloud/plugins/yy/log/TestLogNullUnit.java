package info.xiancloud.plugins.yy.log;

import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.test.TestGroup;
import info.xiancloud.plugin.util.LOG;

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
