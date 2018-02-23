package info.xiancloud.plugin.test.echo_test;

import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.test.TestGroup;

/**
 * @author happyyangyuan
 */
public class EchoUnit implements Unit {

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public String getName() {
        return "echoUnit";
    }

    @Override
    public UnitResponse execute(UnitRequest request) {
        return UnitResponse.success(request.getContext().getBody());
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setBodyRequired(true);
    }
}
