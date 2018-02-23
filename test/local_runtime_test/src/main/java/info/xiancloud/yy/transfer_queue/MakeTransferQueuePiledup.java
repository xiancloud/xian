package info.xiancloud.yy.transfer_queue;

import info.xiancloud.plugin.thread_pool.ThreadPoolManager;
import info.xiancloud.plugin.util.HttpUtil;
import org.apache.http.conn.ConnectTimeoutException;

import java.net.SocketTimeoutException;

/**
 * @author happyyangyuan
 */
public class MakeTransferQueuePiledup {
    public static void main(String[] args) throws ConnectTimeoutException, SocketTimeoutException, InterruptedException {
        for (int i = 0; i < 20000; i++) {
            ThreadPoolManager.execute(() -> HttpUtil.postWithEmptyHeader("http://localhost:9124/v1.0/testService/testTransferable", ""));
            Thread.sleep(100);
        }
    }
}
