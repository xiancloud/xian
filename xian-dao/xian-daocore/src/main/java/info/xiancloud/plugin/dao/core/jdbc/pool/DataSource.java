package info.xiancloud.plugin.dao.core.jdbc.pool;

import java.sql.Connection;

/**
 * mysql数据源
 *
 * @author happyyangyuan
 */
public abstract class DataSource {

    protected String url;
    protected String user;
    protected String pwd;
    //从数据库拿取连接的超时时间
    protected final long connectionAcquisitionTimeout = 5 * 1000L;

    public abstract Connection getConnection();

    public abstract void destroy();

    //解析url得到主机域名
    public String getHost() {
        String hostAndPort = hostAndPort();
        int indexOfMaohao = hostAndPort.indexOf(":");
        if (indexOfMaohao == -1) {
            return hostAndPort;
        } else {
            return hostAndPort.substring(0, indexOfMaohao);
        }
    }

    //解析url得到端口号
    public int getPort() {
        String hostAndPort = hostAndPort();
        int indexOfMaohao = hostAndPort.indexOf(":");
        if (indexOfMaohao == -1) {
            return 3306;
        } else {
            return Integer.parseInt(hostAndPort.substring(indexOfMaohao + 1));
        }
    }

    private String hostAndPort() {
        int start = url.indexOf("://") + 3;
        int end = url.indexOf("/", start);
        return url.substring(start, end);
    }

    //解析url得到数据库名称
    public String getDatabase() {
        int indexSlash = url.indexOf("/", url.indexOf("://") + 3);
        int questionMarkIndex = url.indexOf("?");
        return url.substring(indexSlash + 1, questionMarkIndex);
    }

    /**
     * 忙碌的连接数
     */
    public abstract int getActiveConnectionCount();

    /**
     * 获取连接池最大连接数(虽然是个配置值是已知的，但监控统计需要用到，因此提供这个接口出去)
     */
    public abstract int getPoolSize();

}
