package info.xiancloud.yy.unit_srialize;

import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.test.TestGroup;

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
                .createWithDescription("测试unit序列化指定属性")
                .setDocApi(false);
    }

    @Override
    public Input getInput() {
        return null;
    }

    public String getOtherAnyThing() {
        return "kk";
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        handler.handle(null);
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
