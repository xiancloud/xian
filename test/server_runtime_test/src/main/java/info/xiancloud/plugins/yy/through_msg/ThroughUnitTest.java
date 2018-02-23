package info.xiancloud.plugins.yy.through_msg;

import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.test.TestGroup;

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
