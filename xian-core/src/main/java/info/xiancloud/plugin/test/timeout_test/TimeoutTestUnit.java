package info.xiancloud.plugin.test.timeout_test;

import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.test.TestGroup;
import info.xiancloud.plugin.util.LOG;

/**
 * @author happyyangyuan
 */
public class TimeoutTestUnit implements Unit {
    @Override
    public String getName() {
        return "timeoutTest";
    }

    @Override
    public Input getInput() {
        return new Input();
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        try {
            Thread.sleep(Constant.UNIT_DEFAULT_TIME_OUT_IN_MILLI + 1000);
        } catch (InterruptedException e) {
            LOG.error(e);
        }
        return UnitResponse.error(Group.CODE_TIME_OUT, null, null);
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
