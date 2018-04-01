package info.xiancloud.rules.test.uriParamTestRule;

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
        return UnitResponse.createSuccess(msg.argJson());
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
