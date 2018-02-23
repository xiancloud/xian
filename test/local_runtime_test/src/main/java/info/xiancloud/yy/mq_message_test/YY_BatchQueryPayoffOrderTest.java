package info.xiancloud.yy.mq_message_test;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.socket.ConnectTimeoutException;
import info.xiancloud.plugin.util.HttpUtil;

import java.net.SocketTimeoutException;

/**
 * @author happyyangyuan
 */
public class YY_BatchQueryPayoffOrderTest {
    public static void main(String[] args) throws InterruptedException {
        for (int j = 0; j < 5; j++) {
            Thread t = new Thread(() -> {
                for (int i = 0; i < 111000; i++) {
                    final int pageNumber = i;
                    JSONObject body = new JSONObject() {{
                        put("pageSize", 300);
                        put("pageNumber", pageNumber);
                        put("startDate", "2017-05-10");
                        put("endDate", "2017-05-20");
                    }};
                    try {
                        HttpUtil.postWithEmptyHeader("http://api3.xiancloud.cn/v1.0/payoffService/getPayoffOrder", body.toJSONString());
                    } catch (SocketTimeoutException | ConnectTimeoutException e) {
                        e.printStackTrace();
                    }
                    /*try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                }
            });
            t.start();
        }

        for (int j = 0; j < 5; j++) {
            Thread t = new Thread(() -> {
                for (int i = 0; i < 1000000; i++) {
                    JSONObject body = new JSONObject() {{
                        put("macs", new String[]{"863613039476123", "863613039478314"});
                    }};
                    try {
                        HttpUtil.postWithEmptyHeader("http://api3.xiancloud.cn/v1.0/m2mTongXinService/checkMultipleOnline", body.toJSONString());
                    } catch (SocketTimeoutException | ConnectTimeoutException e) {
                        e.printStackTrace();
                    }
                    /*try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                }
            });
            t.start();
        }
    }
}
