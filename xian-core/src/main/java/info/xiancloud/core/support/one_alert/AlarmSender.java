package info.xiancloud.core.support.one_alert;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.HttpUtil;
import info.xiancloud.core.util.thread.MsgIdHolder;
import io.reactivex.Single;

/**
 * @author happyyangyuan
 * @deprecated one apm is not used any more.
 */
public class AlarmSender {
    public static Single<String> alarm(String alarmName, String alarmContent) {
        /*if (!EnvUtil.PRODUCTION.equals(EnvUtil.getEnv())) {
            LOG.warn(new JSONObject()
                    .fluentPut("description", "No alerts for non production.")
                    .fluentPut("alarmName", alarmName)
                    .fluentPut("alarmContent", alarmContent));
            return Single.just("No alerts for non production.");
        }*/
        String body = new JSONObject() {{
            put("app", "3ac475e7-9f9e-2279-429f-3257d9084c84");
            put("eventId", MsgIdHolder.get() == null ? MsgIdHolder.init() : MsgIdHolder.get());
            put("eventType", "trigger");
            put("alarmName", "[" + EnvUtil.getEnv() + "]" + alarmName);
            put("alarmContent", alarmContent);
        }}.toJSONString();
        return HttpUtil.postWithEmptyHeader("http://api.110monitor.com/alert/api/event", body);
    }

}
