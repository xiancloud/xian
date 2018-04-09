package info.xiancloud.plugins.yy.shutdown_gracefully_test;

import info.xiancloud.core.*;
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
        return UnitMeta.createWithDescription("测试服务停止时，尚未结束的unit可以优雅的运行完毕")
                .setDocApi(false);
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public void execute(UnitRequest request, Handler<UnitResponse> handler) {
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
        }
        handler.handle(UnitResponse.createSuccess());
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }

}
