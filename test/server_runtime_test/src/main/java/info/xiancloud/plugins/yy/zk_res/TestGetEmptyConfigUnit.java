package info.xiancloud.plugins.yy.zk_res;

import info.xiancloud.plugin.*;
import info.xiancloud.plugin.conf.XianConfig;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.test.TestGroup;

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
        return UnitMeta.create().setPublic(false);
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        return UnitResponse.success(XianConfig.get("whatever"));
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
