package info.xiancloud.dao.core.pool;

/**
 * @author happyyangyuan
 */
public abstract class AbstractPool implements IPool {

    /**
     * read-write datasource
     */
    protected XianDataSource writableDataSource;
    /**
     * read-only datasource
     */
    protected XianDataSource readOnlyDataSource;
    /**
     * status of this pool, initialized or not.
     */
    protected volatile boolean initialized = false;

    @Override
    public void destroyPoolIfNot() {
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
