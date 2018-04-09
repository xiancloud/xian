package info.xiancloud.plugin.dao.core.test.transferable;

import info.xiancloud.core.*;
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
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        handler.handle(UnitResponse.createSuccess("中转成功."));
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
