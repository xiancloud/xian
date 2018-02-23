package info.xiancloud.yy.xmx_test;

import info.xiancloud.plugin.socket.ConnectTimeoutException;
import info.xiancloud.plugin.thread_pool.ThreadPoolManager;
import info.xiancloud.plugin.util.HttpUtil;

import java.net.SocketTimeoutException;

/**
 * @author happyyangyuan
 */
public class XmxTest {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            ThreadPoolManager.execute(() -> {
                try {
                    for (int j = 0; j < 1000; j++) {
                        HttpUtil.post("http://127.0.0.1:9124/v/testService/echoUnit", "", null);
                    }
                } catch (ConnectTimeoutException | SocketTimeoutException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
