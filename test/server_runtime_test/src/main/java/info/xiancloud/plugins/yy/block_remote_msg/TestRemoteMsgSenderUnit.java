package info.xiancloud.plugins.yy.block_remote_msg;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.SyncXian;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.test.TestGroup;

/**
 * 屏蔽远程消息发送的功能点的测试，发送者
 *
 * @author happyyangyuan
 */
public class TestRemoteMsgSenderUnit implements Unit {
    @Override
    public String getName() {
        return "testRemoteMsgSender";
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        return SyncXian.call("test", "testRemoteMsgReceiver", new JSONObject());
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
