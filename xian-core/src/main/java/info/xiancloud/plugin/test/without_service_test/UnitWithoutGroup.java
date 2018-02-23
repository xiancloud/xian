package info.xiancloud.plugin.test.without_service_test;

import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;

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
    public UnitResponse execute(UnitRequest msg) {
        return null;
    }

}
