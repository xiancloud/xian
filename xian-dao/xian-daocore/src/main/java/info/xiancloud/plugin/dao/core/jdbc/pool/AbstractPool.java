package info.xiancloud.plugin.dao.core.jdbc.pool;

/**
 * @author happyyangyuan
 */
public abstract class AbstractPool implements IPool {

    protected DataSource writableDataSource;
    protected DataSource readOnlyDataSource;
    protected boolean initialized = false;

    @Override
    public void destroyPool() {
        try {
            writableDataSource.destroy();
            writableDataSource = null;
            if (READ_WRITE_SEPARATED) {
                readOnlyDataSource.destroy();
                readOnlyDataSource = null;
            }
        } finally {
            initialized = false;
        }
    }
}
