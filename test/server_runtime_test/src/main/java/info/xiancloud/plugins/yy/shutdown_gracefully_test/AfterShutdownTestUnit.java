package info.xiancloud.plugins.yy.shutdown_gracefully_test;

import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.test.TestGroup;

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
        return UnitResponse.success();
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }

}
