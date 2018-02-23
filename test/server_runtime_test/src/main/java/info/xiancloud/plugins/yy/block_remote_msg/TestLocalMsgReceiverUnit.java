package info.xiancloud.plugins.yy.block_remote_msg;

import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.test.TestGroup;

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
        return UnitResponse.success();
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
