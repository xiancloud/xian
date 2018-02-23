package info.xiancloud.yy.unit_srialize;

import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.test.TestGroup;

/**
 * @author happyyangyuan
 */
public class TestSerializeUnit implements Unit {
    @Override
    public String getName() {
        return "testSerialize";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta
                .create("测试unit序列化指定属性")
                .setPublic(false);
    }

    @Override
    public Input getInput() {
        return null;
    }

    public String getOtherAnyThing() {
        return "kk";
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        return null;
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
