package info.xiancloud.plugin.dao.core.test.transferable;

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
        return UnitResponse.createSuccess("中转成功.");
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
