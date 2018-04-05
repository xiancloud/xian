package info.xiancloud.plugins.yy.block_remote_msg;

import info.xiancloud.core.Group;
import info.xiancloud.core.Handler;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.Xian;
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
    public Group getGroup() {
        return TestGroup.singleton;
    }

    @Override
    public void execute(UnitRequest request, Handler<UnitResponse> handler) {
        Xian.call("test", "testRemoteMsgReceiver", handler::handle);
    }

}
