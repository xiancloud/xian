package info.xiancloud.dao.core.pool;

/**
 * Database connection pool interface.
 * Pool in xian frame can provide both read-write connections and read-only connections.
 * Note:
 * 1. database pool in xian holds both a master datasource and a slave datasource
 * 2. you can obtain master connection from the master datasource and the read-only connection from the slave datasource.
 *
 * @author happyyangyuan
 */
public interface IPool {

    /**
     * read write separation, defaults to false
     */
    boolean READ_WRITE_SEPARATED = false;

    /**
     * Initialize this pool if it is not initialized yet.
     * This is blocking.
     */
    void initPoolIfNot();

    /**
     * destroy this pool and any recycling operation if it is not yet bean destroyed.
     * This is blocking.
     */
    void destroyPoolIfNot();

    /**
     * getter for the master datasource.
     *
     * @return the master datasource
     */
    XianDataSource getMasterDatasource();

    /**
     * getter for the slave datasource
     *
     * @return the slave datasource
     */
    XianDataSource getSlaveDatasource();

    /**
     * check whether this pool has bean initialized.
     *
     * @return true or false
     */
    boolean isInitialized();

}
