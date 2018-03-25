package info.xiancloud.httpclient.apache_http.pool;

import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.util.LOG;
import org.apache.http.conn.HttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

public class IdlePoolMonitor {

    private final static long IDLETIME = 5000;// 多久检查一次
    private final static int CLOSEEXPIRE = 30; // 空闲时间超过30秒就回收

    private static HttpClientConnectionManager pool;

    public static IdlePoolMonitor build(HttpClientConnectionManager connMgr) {
        pool = connMgr;
        return new IdlePoolMonitor();
    }

    public void monitor() {
        if (pool == null)
            throw new IllegalArgumentException("apache_httpclient连接池不能为空---");

        ThreadPoolManager.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    pool.closeExpiredConnections();
                    pool.closeIdleConnections(CLOSEEXPIRE, TimeUnit.SECONDS);
                } catch (Exception e) {
                    LOG.error("apache_httpclient连接池长连接定时回收出错 : " + e);
                }
            }
        }, IDLETIME);
    }
}
