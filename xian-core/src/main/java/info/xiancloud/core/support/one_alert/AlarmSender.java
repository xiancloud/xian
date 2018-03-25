package info.xiancloud.core.support.one_alert;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.socket.ConnectTimeoutException;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.HttpUtil;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.thread.MsgIdHolder;

import java.net.SocketTimeoutException;

/**
 * @author happyyangyuan
 */
public class AlarmSender {
    public static void alarm(String alarmName, String alarmContent) {
        if (!EnvUtil.PRODUCTION.equals(EnvUtil.getEnv())) {
            LOG.warn("非生产环境不报警");
            return;
        }
        String body = new JSONObject() {{
            put("app", "3ac475e7-9f9e-2279-429f-3257d9084c84");
            put("eventId", MsgIdHolder.get() == null ? MsgIdHolder.init() : MsgIdHolder.get());
            put("eventType", "trigger");
            put("alarmName", "[" + EnvUtil.getEnv() + "]" + alarmName);
            put("alarmContent", alarmContent);
        }}.toJSONString();
        try {
            HttpUtil.postWithEmptyHeader("http://api.110monitor.com/alert/api/event", body);
        } catch (SocketTimeoutException | ConnectTimeoutException e) {
            LOG.error(e);
        }
    }

}
