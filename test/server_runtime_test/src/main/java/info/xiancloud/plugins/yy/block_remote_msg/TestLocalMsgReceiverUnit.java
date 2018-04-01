package info.xiancloud.plugins.yy.block_remote_msg;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
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
    public Input getInput() {
        return null;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        return UnitResponse.createSuccess();
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
