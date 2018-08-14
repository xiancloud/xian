package info.xiancloud.dao.jdbc.pool.druid;

import com.alibaba.druid.pool.GetConnectionTimeoutException;
import info.xiancloud.dao.core.connection.XianConnection;
import info.xiancloud.dao.core.pool.XianDataSource;

import java.sql.SQLException;

/**
 * @author happyyangyuan
 */
public class DruidDataSource extends XianDataSource {

    private com.alibaba.druid.pool.DruidDataSource internalDataSource;

    DruidDataSource(String url, String user, String pwd, int poolSize) {
        this.url = url;
        this.user = user;
        this.pwd = pwd;
        internalDataSource = new com.alibaba.druid.pool.DruidDataSource();
        internalDataSource.setUrl(url);
        internalDataSource.setUsername(user);
        internalDataSource.setPassword(pwd);
        //pool size
        internalDataSource.setMaxActive(poolSize);
        //initial size
        internalDataSource.setInitialSize(1);
        //min idle connection in the pool
        internalDataSource.setMinIdle(1);
        //make sure connections held in the pool is always valid.
        internalDataSource.setValidationQuery("select now()");
        //the same as above
        internalDataSource.setTestWhileIdle(true);
        //for performance consideration
        internalDataSource.setTestOnBorrow(false);
        //for performance consideration
        internalDataSource.setTestOnReturn(false);
        //time between the eviction thread to check idle connections.
        internalDataSource.setTimeBetweenEvictionRunsMillis(1000 * 60);
        //set the max idle time, make sure sleeping connections release as early as possible.
        internalDataSource.setMinEvictableIdleTimeMillis(1000 * 60 * 2);
    }

    @Override
    public XianConnection getConnection() {
        try {
            return internalDataSource.getConnection(connectionAcquisitionTimeout);
        } catch (GetConnectionTimeoutException e) {
            throw new RuntimeException("获取数据库连接超时", e);
        } catch (SQLException e) {
            throw new RuntimeException("数据库未知异常", e);
        }
    }

    @Override
    public void destroy() {
        internalDataSource.close();
    }

    @Override
    public int getActiveConnectionCount() {
        return internalDataSource.getActiveCount();
    }

    @Override
    public int getPoolSize() {
        return internalDataSource.getMaxActive();
    }

}
