package info.xiancloud.dao.jdbc.pool.druid;

import info.xiancloud.dao.jdbc.pool.DataSource;
import info.xiancloud.dao.jdbc.pool.DatasourceConfigReader;
import info.xiancloud.dao.jdbc.pool.AbstractPool;

import java.sql.Connection;

/**
 * 阿里druid连接池,由于我们使用的是腾讯云cdb,不需要我们内部使用读写分离所以这里不提供读写分离数据源
 *
 * @author happyyangyuan
 */
public class DruidPool extends AbstractPool {

    @Override
    public synchronized void initPool() {
        if (!initialized) {
            writableDataSource = new DruidDataSource(DatasourceConfigReader.getWriteUrl(),
                    DatasourceConfigReader.getWriteUser(),
                    DatasourceConfigReader.getWritePwd(),
                    DatasourceConfigReader.getWritePoolSize());
            if (!READ_WRITE_SEPARATED) {
                readOnlyDataSource = writableDataSource;
            } else {
                readOnlyDataSource = new DruidDataSource(DatasourceConfigReader.getReadUrl(),
                        DatasourceConfigReader.getReadUser(),
                        DatasourceConfigReader.getReadPwd(),
                        DatasourceConfigReader.getReadPoolSize());
            }
        }
        initialized = true;
    }

    @Override
    public DataSource getMasterDatasource() {
        if (!initialized) {
            initPool();
        }
        return writableDataSource;
    }

    @Override
    public DataSource getSlaveDatasource() {
        if (!initialized) {
            initPool();
        }
        return readOnlyDataSource;
    }

    @Override
    public Connection getReadConnection() {
        return getSlaveDatasource().getConnection();
    }

    @Override
    public Connection getWriteConnection() {
        return getMasterDatasource().getConnection();
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

}
