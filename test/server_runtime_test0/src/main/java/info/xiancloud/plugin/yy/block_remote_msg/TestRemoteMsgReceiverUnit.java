package info.xiancloud.plugin.yy.block_remote_msg;

import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.test.TestGroup;

/**
 * 远程消息发送的功能点的测试，接收者
 *
 * @author happyyangyuan
 */
public class TestRemoteMsgReceiverUnit implements Unit {
    @Override
    public String getName() {
        return "testRemoteMsgReceiver";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("接收者");
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public void execute(UnitRequest request, Handler<UnitResponse> handler) {
        handler.handle(UnitResponse.createSuccess("YY : received"));
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
