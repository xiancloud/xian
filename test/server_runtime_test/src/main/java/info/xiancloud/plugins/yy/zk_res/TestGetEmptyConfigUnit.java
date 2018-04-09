package info.xiancloud.plugins.yy.zk_res;

import info.xiancloud.core.*;
import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.test.TestGroup;

/**
 * @author happyyangyuan
 */
public class TestGetEmptyConfigUnit implements Unit {
    @Override
    public String getName() {
        return "testGetEmptyConfig";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDocApi(false);
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        handler.handle(UnitResponse.createSuccess(XianConfig.get("whatever")));
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
