package info.xiancloud.yy.sender;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.message.SyncXian;

/**
 * @author happyyangyuan
 */
public class TestSequential {

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            SyncXian.call("httpClient", "apacheHttpClientPost", new JSONObject() {{
                put("url", "http://localhost:9124/v1.0/testService/testSequentialQueue");
                put("body", new JSONObject() {{
                    put("sequentialParam", 1234);
                    put("nano", System.nanoTime());
                }}.toJSONString());
                put("readTimeoutInMillis", 50);
            }});
        }

    }
}
