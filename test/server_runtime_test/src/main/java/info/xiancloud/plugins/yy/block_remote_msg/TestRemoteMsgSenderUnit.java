package info.xiancloud.plugins.yy.block_remote_msg;

import info.xiancloud.core.*;
import info.xiancloud.core.message.SingleRxXian;
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
    public Group getGroup() {
        return TestGroup.singleton;
    }

    @Override
    public void execute(UnitRequest request, Handler<UnitResponse> handler) {
        SingleRxXian
                .call("test", "testRemoteMsgReceiver")
                .subscribe(handler::handle);
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDocApi(false);
    }
}
