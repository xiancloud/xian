package info.xiancloud.plugins.yy.unit_execution_exception;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.Xian;
import info.xiancloud.core.test.TestGroup;

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
            protected void handle(UnitResponse unitResponse) {
                System.out.println(unitResponse.toString());
            }
        });
    }
}
