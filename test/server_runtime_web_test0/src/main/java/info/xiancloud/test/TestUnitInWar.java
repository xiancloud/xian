package info.xiancloud.test;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.test.TestGroup;

/**
 * test unit in war file.
 *
 * @author happyyangyuan
 */
public class TestUnitInWar implements Unit {
    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }

    @Override
    public UnitResponse execute(UnitRequest request) {
        return UnitResponse.success("happy ending.");
    }
}
