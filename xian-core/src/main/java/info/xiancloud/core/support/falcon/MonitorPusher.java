package info.xiancloud.core.support.falcon;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.util.LOG;

/**
 * @author happyyangyuan
 */
public class MonitorPusher {
    /**
     * push metric to open falcon server
     *
     * @param metric   metric
     * @param interval metric step
     * @param value    the value, can be java bean,or map, or json object, or json array, or a number
     */
    public static void push(String metric, int interval, Object value) {
        SingleRxXian.call("monitor", "pushToFalcon", new JSONObject() {{
            put("metric", metric);
            put("step", interval);
            put("value", value);
        }}).subscribe(unitResponse -> LOG.info("监控数据推送完毕:" + unitResponse));
    }
}
