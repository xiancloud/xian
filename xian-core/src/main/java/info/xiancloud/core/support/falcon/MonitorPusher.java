package info.xiancloud.core.support.falcon;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.Xian;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.util.LOG;

/**
 * @author happyyangyuan
 */
public class MonitorPusher {
    public static void push(String metric, int interval, Object value) {
        Xian.call("monitor", "pushToFalcon", new JSONObject() {{
            put("metric", metric);
            put("step", interval);
            put("value", value);
        }}, new NotifyHandler() {
            protected void handle(UnitResponse unitResponse) {
                LOG.info("监控数据推送完毕:" + unitResponse);
            }
        });
    }
}
