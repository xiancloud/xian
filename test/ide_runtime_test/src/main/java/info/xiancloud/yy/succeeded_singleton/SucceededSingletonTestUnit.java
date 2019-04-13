package info.xiancloud.yy.succeeded_singleton;

import info.xiancloud.core.Group;
import info.xiancloud.core.Handler;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.test.TestGroup;

/**
 * Test for deprecated method {@link UnitResponse#succeededSingleton()}
 */
public class SucceededSingletonTestUnit implements Unit {
    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }

    @Override
    public void execute(UnitRequest request, Handler<UnitResponse> handler) throws Exception {
        // this produces an illegal argument exception, because msgId in succeededSingleton is null.
        handler.handle(UnitResponse.succeededSingleton());
    }

    public static void main(String[] args) {
        SingleRxXian.call(SucceededSingletonTestUnit.class).blockingGet().dataToException().printStackTrace();
    }
}
