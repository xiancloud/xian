package info.xiancloud.httpclient.apache_http.pool;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * Http连接池
 * 
 * @author yyq
 *
 */
public class ApacheHttpConnManager {

	private static volatile PoolingHttpClientConnectionManager Holder;

	public static PoolingHttpClientConnectionManager create() {
		if (Holder == null) {
			synchronized (ApacheHttpConnManager.class) {
				if (Holder == null) {
					PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
					cm.setMaxTotal(500);
					cm.setDefaultMaxPerRoute(200);
					Holder = cm;
					// 连接失效监控和回收
					IdlePoolMonitor.build(cm).monitor();
				}
			}
		}
		return Holder;
	}

	/**
	 * 获取空闲的连接数
	 * 
	 * @return
	 */
	public static int getAvailable() {
		return Holder.getTotalStats().getAvailable();
	}

	/**
	 * 获取等待连接的队列数
	 * 
	 * @return
	 */
	public static int getPending() {
		return Holder.getTotalStats().getPending();
	}

	/**
	 * 获取正在执行的连接数
	 * 
	 * @return
	 */
	public static int getLeased() {
		return Holder.getTotalStats().getLeased();
	}
}
