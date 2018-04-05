package info.xiancloud.core.test.without_service_test;

import info.xiancloud.core.Handler;
import info.xiancloud.core.Input;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;

/**
 * This is verification unit to validate the unit scanner.
 *
 * @author happyyangyuan
 */
public class UnitWithoutGroup extends AbstractUnitWithoutGroup {

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public String getName() {
        return "unitWithoutGroup";
    }

    @Override
    public void execute(UnitRequest request, Handler<UnitResponse> handler) {
    }

}
