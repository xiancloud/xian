package info.xiancloud.rules.test.uriParamTestRule;

import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.test.TestGroup;

/**
 * @author happyyangyuan
 */
public class TestUriParamUnit implements Unit {
    @Override
    public String getName() {
        return "testUriParam";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDocApi(false);
    }

    @Override
    public Input getInput() {
        return new Input().add("myParam", String.class, "测试uri参数的参数");
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        handler.handle(UnitResponse.createSuccess(msg.argJson()));
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
