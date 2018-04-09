package info.xiancloud.plugins.yy.block_remote_msg;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Group;
import info.xiancloud.core.Handler;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.test.TestGroup;

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
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        SingleRxXian
                .call("test", "testLocalMsgReceiver", new JSONObject())
                .subscribe(handler::handle);
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
