package info.xiancloud.plugins.yy.block_remote_msg;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.message.SyncXian;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.test.TestGroup;

/**
 * @author happyyangyuan
 */
public class TestLocalMsgSenderUnit implements Unit {
    @Override
    public String getName() {
        return "testLocalMsgSender";
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        return SyncXian.call("test", "testLocalMsgReceiver", new JSONObject());
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
