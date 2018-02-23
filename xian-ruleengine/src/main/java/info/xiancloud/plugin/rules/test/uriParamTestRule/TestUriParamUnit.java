package info.xiancloud.plugin.rules.test.uriParamTestRule;

import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.test.TestGroup;

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
        return UnitMeta.create().setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input().add("myParam", String.class, "测试uri参数的参数");
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        return UnitResponse.success(msg.argJson());
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
