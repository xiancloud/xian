package info.xiancloud.plugin.yy.block_remote_msg;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.test.TestGroup;

/**
 * 屏蔽远程消息发送的功能点的测试，接收者
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
    public UnitResponse execute(UnitRequest msg) {
        return UnitResponse.success("YY : received");
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
