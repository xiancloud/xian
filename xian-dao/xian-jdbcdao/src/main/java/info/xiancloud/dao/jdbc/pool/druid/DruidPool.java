package info.xiancloud.dao.jdbc.pool.druid;


import info.xiancloud.dao.core.pool.AbstractPool;
import info.xiancloud.dao.core.pool.DatasourceConfigReader;
import info.xiancloud.dao.core.pool.XianDataSource;

/**
 * 阿里druid连接池,由于我们使用的是腾讯云cdb,不需要我们内部使用读写分离所以这里不提供读写分离数据源
 *
 * @author happyyangyuan
 */
public class DruidPool extends AbstractPool {

    @Override
    public synchronized void initPoolIfNot() {
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
    public XianDataSource getMasterDatasource() {
        if (!initialized) {
            initPoolIfNot();
        }
        return writableDataSource;
    }

    @Override
    public XianDataSource getSlaveDatasource() {
        if (!initialized) {
            initPoolIfNot();
        }
        return readOnlyDataSource;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

}
