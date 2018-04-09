package info.xiancloud.plugins.yy.broadcast;

import info.xiancloud.core.*;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.test.TestGroup;

/**
 * @author happyyangyuan
 */
public class TestBroadcastUnit implements Unit {
    @Override
    public String getName() {
        return "testBroadcast";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("测试广播型unit").setBroadcast(UnitMeta.Broadcast.create().setAsync(false));
    }

    @Override
    public Input getInput() {
        return new Input();
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        handler.handle(UnitResponse.createSuccess(LocalNodeManager.LOCAL_NODE_ID));
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }

}
