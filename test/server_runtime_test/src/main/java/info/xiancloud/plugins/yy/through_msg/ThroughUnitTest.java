package info.xiancloud.plugins.yy.through_msg;

import info.xiancloud.core.*;
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
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        handler.handle(UnitResponse.createSuccess("what you see is what you get."));
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
