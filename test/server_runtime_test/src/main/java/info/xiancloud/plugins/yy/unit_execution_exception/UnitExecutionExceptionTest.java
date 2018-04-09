package info.xiancloud.plugins.yy.unit_execution_exception;

import info.xiancloud.core.Group;
import info.xiancloud.core.Handler;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
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
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        throw new RuntimeException("test execution exception.");
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }

    public static void main(String[] args) {
        SingleRxXian
                .call(UnitExecutionExceptionTest.class)
                .subscribe(unitResponse -> System.out.println(unitResponse.toString()))
        ;
    }
}
