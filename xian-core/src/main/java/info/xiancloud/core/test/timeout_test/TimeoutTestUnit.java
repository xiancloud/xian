package info.xiancloud.core.test.timeout_test;

import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.test.TestGroup;
import info.xiancloud.core.util.LOG;

/**
 * @author happyyangyuan
 */
public class TimeoutTestUnit implements Unit {
    @Override
    public String getName() {
        return "timeoutTest";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDocApi(false);
    }

    @Override
    public Input getInput() {
        return new Input();
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        try {
            Thread.sleep(Constant.UNIT_DEFAULT_TIME_OUT_IN_MILLI + 1000);
        } catch (InterruptedException e) {
            LOG.error(e);
        }
        handler.handle(UnitResponse.createError(Group.CODE_TIME_OUT, null, null));
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
