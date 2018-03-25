package info.xiancloud.yy.transfer_queue;

import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.util.HttpUtil;

/**
 * @author happyyangyuan
 */
public class MakeTransferQueuePiledup {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 20000; i++) {
            ThreadPoolManager.execute(() -> HttpUtil.postWithEmptyHeader("http://localhost:9124/v1.0/testService/testTransferable", ""));
            Thread.sleep(100);
        }
    }
}
