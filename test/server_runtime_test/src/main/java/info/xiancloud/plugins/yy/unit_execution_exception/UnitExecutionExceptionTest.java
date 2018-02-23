package info.xiancloud.plugins.yy.unit_execution_exception;

import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.Xian;
import info.xiancloud.plugin.support.mq.mqtt.handle.NotifyHandler;
import info.xiancloud.plugin.test.TestGroup;

/**
 * @author happyyangyuan
 */
public class UnitExecutionExceptionTest implements Unit {
    @Override
    public String getName() {
        return "unitExecutionExceptionTest";
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        throw new RuntimeException("test execution exception.");
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }

    public static void main(String[] args) {
        Xian.call(UnitExecutionExceptionTest.class, new NotifyHandler() {
            @Override
            protected void toContinue(UnitResponse unitResponse) {
                System.out.println(unitResponse.toString());
            }
        });
    }
}
