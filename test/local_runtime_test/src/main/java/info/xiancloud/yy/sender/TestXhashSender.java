package info.xiancloud.yy.sender;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.util.HttpUtil;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.RandomUtils;

/**
 * @author happyyangyuan
 */
public class TestXhashSender {

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            ThreadPoolManager.execute(() -> {
                try {
                    System.out.println(
                            JSON.toJSONString(
                                    JSON.parseObject(HttpUtil.postWithEmptyHeader("http://localhost:9124/v1.0/testService/testXhash", new JSONObject() {{
                                        put("x", "zxcd-123-efga");
                                        put("y", RandomUtils.getRandomNumbers(4));
                                    }}.toJSONString())),
                                    true
                            )
                    );
                    System.out.println(
                            JSON.toJSONString(
                                    JSON.parseObject(HttpUtil.postWithEmptyHeader("http://localhost:9124/v1.0/testService/testXhash", new JSONObject() {{
                                        put("x", "abc-123-456");
                                        put("y", RandomUtils.getRandomNumbers(4));
                                    }}.toJSONString())),
                                    true
                            )
                    );
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            });
        }
        LOG.debug("经过几轮测试，基本得出结论，单个xhash参数的一致性可以得到保证");
    }
}
