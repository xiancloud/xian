package info.xiancloud.dao.async.postgresql;

import info.xiancloud.dao.core.pool.AbstractPool;
import info.xiancloud.dao.core.pool.DatasourceConfigReader;
import info.xiancloud.dao.core.pool.XianDataSource;

/**
 * postgresql xian pool
 *
 * @author happyyangyuan
 */
public class PgXianPool extends AbstractPool {
    @Override
    public void initPoolIfNot() {
        if (!initialized) {
            writableDataSource = new PgXianDatasource(DatasourceConfigReader.getWriteUrl(),
                    DatasourceConfigReader.getWriteUser(),
                    DatasourceConfigReader.getWritePwd(),
                    DatasourceConfigReader.getWritePoolSize());
            if (!READ_WRITE_SEPARATED) {
                readOnlyDataSource = writableDataSource;
            } else {
                readOnlyDataSource = new PgXianDatasource(DatasourceConfigReader.getReadUrl(),
                        DatasourceConfigReader.getReadUser(),
                        DatasourceConfigReader.getReadPwd(),
                        DatasourceConfigReader.getReadPoolSize());
            }
        }
        initialized = true;
    }

    @Override
    public XianDataSource getMasterDatasource() {
        return writableDataSource;
    }

    @Override
    public XianDataSource getSlaveDatasource() {
        return readOnlyDataSource;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }
}
