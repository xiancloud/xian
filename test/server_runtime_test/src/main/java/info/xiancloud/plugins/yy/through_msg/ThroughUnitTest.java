package info.xiancloud.plugins.yy.through_msg;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.test.TestGroup;

/**
 * @author happyyangyuan
 */
public class ThroughUnitTest implements Unit {
    @Override
    public String getName() {
        return "throughUnitTest";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create()
                .setPublic(false)
                .setBodyRequired(true)
                .setDataOnly(true);
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        return UnitResponse.success("what you see is what you get.");
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
