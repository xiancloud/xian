package info.xiancloud.plugin.dao.core.jdbc.pool;

import java.sql.Connection;

/**
 * 数据库连接池接口
 *
 * @author happyyangyuan
 */
public interface IPool {

    /**
     * 是否开启读写分离,默认不开启
     */
    boolean READ_WRITE_SEPARATED = false;

    /**
     * @return 读连接
     */
    Connection getReadConnection();

    /**
     * @return 读写连接(如果是读写分离的话, 这里就是写连接)
     */
    Connection getWriteConnection();

    /**
     * 要求子类实现为可以重复初始化的方式,即该方法可以安全的被重复调用
     */
    void initPool();

    void destroyPool();

    DataSource getMasterDatasource();

    DataSource getSlaveDatasource();

    /**
     * 连接池是否已经初始化
     */
    boolean isInitialized();

}
