package info.xiancloud.plugin.http_server.unit;

import info.xiancloud.plugin.util.LOG;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * http server会话本地缓存
 *
 * @author happyyangyuan
 */
public class HttpSessionLocalCache {

    private static Map<String, Object> sessionMap;
    //我们向外暴露出只读map,供session数量监控使用
    private static Map<String, Object> unmodifiableSessionMap;

    static {
        sessionMap = new ConcurrentHashMap<>();
        unmodifiableSessionMap = Collections.unmodifiableMap(sessionMap);
    }

    public static void cacheSession(String $msgId, Object session) {
        sessionMap.put($msgId, session);
    }

    public static Object removeSession(String $msgId) {
        LOG.debug("session remove :" + $msgId);
        return sessionMap.remove($msgId);
    }

    public static Map<String, Object> getSessionMap() {
        return unmodifiableSessionMap;
    }

}
