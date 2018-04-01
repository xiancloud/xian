package info.xiancloud.plugins.yy.shutdown_gracefully_test;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.test.TestGroup;

/**
 * @author happyyangyuan
 */
public class AfterShutdownTestUnit implements Unit {
    @Override
    public String getName() {
        return "afterShutdownTest";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("测试服务停止时，尚未结束的unit可以优雅的运行完毕")
                .setPublic(false);
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
        }
        return UnitResponse.createSuccess();
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }

}
