package info.xiancloud.plugin.dao.core.test.transferable;

import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.test.TestGroup;

/**
 * @author happyyangyuan
 */
public class TestTransferable implements Unit {
    @Override
    public String getName() {
        return "testTransferable";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.
                create()
                .setPublic(false)
                .setTransferable(true);
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        return UnitResponse.success("中转成功.");
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
