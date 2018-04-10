package info.xiancloud.plugins.yy.block_remote_msg;

import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.test.TestGroup;

/**
 * @author happyyangyuan
 */
public class TestLocalMsgReceiverUnit implements Unit {
    @Override
    public String getName() {
        return "testLocalMsgReceiver";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDocApi(false);
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        handler.handle(UnitResponse.createSuccess());
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
