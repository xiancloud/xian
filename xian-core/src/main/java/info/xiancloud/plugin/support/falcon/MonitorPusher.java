package info.xiancloud.plugin.support.falcon;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.Xian;
import info.xiancloud.plugin.support.mq.mqtt.handle.NotifyHandler;
import info.xiancloud.plugin.util.LOG;

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
            protected void toContinue(UnitResponse unitResponse) {
                LOG.info("监控数据推送完毕:" + unitResponse);
            }
        });
    }
}
