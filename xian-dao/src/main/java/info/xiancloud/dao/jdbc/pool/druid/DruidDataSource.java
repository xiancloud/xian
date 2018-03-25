package info.xiancloud.dao.jdbc.pool.druid;

import com.alibaba.druid.pool.GetConnectionTimeoutException;
import info.xiancloud.dao.jdbc.pool.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author happyyangyuan
 */
public class DruidDataSource extends DataSource {

    private com.alibaba.druid.pool.DruidDataSource dataSource;

    DruidDataSource(String url, String user, String pwd, int poolSize) {
        this.url = url;
        this.user = user;
        this.pwd = pwd;
        dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(pwd);
        dataSource.setMaxActive(poolSize);//最大连接数
        dataSource.setInitialSize(1);//初始大小
        dataSource.setMinIdle(1);//最小连接数
        dataSource.setValidationQuery("select now()");//make sure connections held in the pool is always valid.
        dataSource.setTestWhileIdle(true);//the same as above
        dataSource.setTestOnBorrow(false);//for performance consideration
        dataSource.setTestOnReturn(false);//for performance consideration
        dataSource.setTimeBetweenEvictionRunsMillis(1000 * 60);//time between the eviction thread to check idle connections.
        dataSource.setMinEvictableIdleTimeMillis(1000 * 60 * 2);//set the max idle time, make sure sleeping connections release as early as possible.
    }

    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection(connectionAcquisitionTimeout);
        } catch (GetConnectionTimeoutException e) {
            throw new RuntimeException("获取数据库连接超时", e);
        } catch (SQLException e) {
            throw new RuntimeException("数据库未知异常", e);
        }
    }

    @Override
    public void destroy() {
        dataSource.close();
    }

    @Override
    public int getActiveConnectionCount() {
        return dataSource.getActiveCount();
    }

    @Override
    public int getPoolSize() {
        return dataSource.getMaxActive();
    }

}
