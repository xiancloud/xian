package info.xiancloud.dao.core.pool;

import info.xiancloud.dao.core.connection.XianConnection;
import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Database datasource
 *
 * @author happyyangyuan
 */
public abstract class XianDataSource {

    /**
     * this full url for the datasource except the username and password.
     * implementations must obey the qualified url specification:
     * eg. mysql://host:port/database?abc=efg&hi=000;
     * eg. postgresql://host:port/database?abc=efg&lll=111;
     * This is mysql standard and xian's dao plugin obeys it. Other datasource should abey it too.
     */
    protected String url;
    protected String user;
    protected String pwd;
    /**
     * time out in milliseconds for acquiring connection.
     */
    protected final long connectionAcquisitionTimeout = 5 * 1000L;

    /**
     * here we design this: connections are orignally provided by datasource but by pool.
     *
     * @return xian connection.
     */
    public abstract Single<XianConnection> getConnection();

    public abstract Completable destroy();

    /**
     * parse database url and get the host.
     *
     * @return the database host
     */
    public String getHost() {
        String hostAndPort = hostAndPort();
        int indexOfMaohao = hostAndPort.indexOf(":");
        if (indexOfMaohao == -1) {
            return hostAndPort;
        } else {
            return hostAndPort.substring(0, indexOfMaohao);
        }
    }

    /**
     * parse the database url and get the port.
     *
     * @return the port
     */
    public int getPort() {
        String hostAndPort = hostAndPort();
        int indexOfMaohao = hostAndPort.indexOf(":");
        if (indexOfMaohao == -1) {
            return 3306;
        } else {
            return Integer.parseInt(hostAndPort.substring(indexOfMaohao + 1));
        }
    }

    /**
     * parse the url and get the host and port string.
     *
     * @return the host:port string
     */
    private String hostAndPort() {
        int start = url.indexOf("://") + 3;
        int end = url.indexOf("/", start);
        return url.substring(start, end);
    }

    /**
     * parse the databse url and get he database name
     *
     * @return the database name
     */
    public String getDatabase() {
        int indexSlash = url.indexOf("/", url.indexOf("://") + 3);
        int questionMarkIndex = url.indexOf("?");
        return url.substring(indexSlash + 1, questionMarkIndex);
    }

    /**
     * getter for the count of busy connections
     *
     * @return count of busy connections
     */
    public abstract int getActiveConnectionCount();

    /**
     * Get the pool size.
     * for monitoring.
     *
     * @return pool size.
     */
    public abstract int getPoolSize();

}
