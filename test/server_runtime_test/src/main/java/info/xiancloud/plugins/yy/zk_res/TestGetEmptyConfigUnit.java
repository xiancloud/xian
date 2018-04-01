package info.xiancloud.plugins.yy.zk_res;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
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
        return UnitMeta.create().setPublic(false);
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        return UnitResponse.createSuccess(XianConfig.get("whatever"));
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
