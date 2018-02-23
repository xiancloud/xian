package info.xiancloud.graylog2.unit;

import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.thread_pool.ThreadPoolManager;
import info.xiancloud.plugin.util.LOG;

import java.util.concurrent.CountDownLatch;

/**
 * @author happyyangyuan
 */
public class TestGraylogClientUnit implements Unit {
    @Override
    public String getName() {
        return "TestGraylogClientUnit";
    }

    @Override
    public Group getGroup() {
        return GraylogService.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("测试graylog日志输出").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("count", int.class, "总日志条数", REQUIRED)
                .add("tCount", int.class, "线程数", REQUIRED);
    }

    //返回的是执行的ms数
    public UnitResponse execute(UnitRequest msg) {
        return UnitResponse.success(test(msg.get("count", int.class), msg.get("tCount", int.class)));
    }

    public static long test(int count, int tCount) {
        final int pCount = count / tCount;
        long start = System.currentTimeMillis();
        CountDownLatch la = new CountDownLatch(tCount);
        for (int i = 0; i < tCount; i++) {
            ThreadPoolManager.execute(() -> {
                for (int j = 0; j < pCount; j++) {
                    LOG.info("歪歪测试graylog客户端性能");
                }
                la.countDown();
            });
        }
        try {
            la.await();
        } catch (InterruptedException e) {
            LOG.error(e);
        }
        return System.currentTimeMillis() - start;
    }
}
