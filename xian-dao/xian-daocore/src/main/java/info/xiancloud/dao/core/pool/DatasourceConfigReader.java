package info.xiancloud.dao.core.pool;

import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.util.LOG;

/**
 * Datasource configuration reader. Datasource configuration property file demo: <br>
 * <p>
 * <code>
 * db_url=
 * db_user=
 * db_pwd=
 * db_pool_size=
 * readonly_db_url=
 * readonly_db_user=
 * readonly_db_pwd=
 * readonly_db_pool_size=
 * db_transaction_timeout=1000*60*60
 * </code>
 * </p>
 *
 * @author happyyangyuan
 */
public class DatasourceConfigReader {

    /**
     * configuration key of db url for writing and reading
     */
    public static final String DB_URL_KEY = "db_url";
    /**
     * configuration key of db user name for writing and reading
     */
    public static final String DB_USER_KEY = "db_user";
    /**
     * configuration key of db password for writing and reading
     */
    public static final String DB_PWD_KEY = "db_pwd";
    /**
     * configuration key of db pool size for writing and reading
     */
    public static final String DB_POOL_SIZE = "db_pool_size";
    /**
     * configuration key of db url for reading only
     */
    public static final String READONLY_DB_URL = "readonly_db_url";
    /**
     * configuration key of db user name for reading only
     */
    public static final String READONLY_DB_USER = "readonly_db_user";
    /**
     * configuration key of db password for reading only
     */
    public static final String READONLY_DB_PWD = "readonly_db_pwd";
    /**
     * configuration key of db pool size for reading only
     */
    public static final String READONLY_DB_POOL_SIZE = "readonly_db_pool_size";
    /**
     * configuration key of db transaction timeout
     */
    public static final String DB_TRANSACTION_TIMEOUT = "db_transaction_timeout";

    /**
     * read master datasource configuration from xian config.
     * please refer to {@link XianDataSource#url}  to see database configuration format
     *
     * @return datasource configuration url
     */
    public static String getWriteUrl() {
        String writeUrl = XianConfig.get(DB_URL_KEY);
        LOG.info("db_url = " + writeUrl);
        return writeUrl;
    }

    public static String getWriteUser() {
        return XianConfig.get(DB_USER_KEY);
    }

    public static String getWritePwd() {
        return XianConfig.get(DB_PWD_KEY);
    }

    public static int getWritePoolSize() {
        return XianConfig.getIntValue(DB_POOL_SIZE);
    }

    /**
     * read slave datasource configuration from xian config.
     * please refer to {@link XianDataSource#url}  to see database configuration format
     *
     * @return datasource configuration url
     */
    public static String getReadUrl() {
        String readUrl = XianConfig.get(READONLY_DB_URL);
        LOG.info("readonly_db_url = " + readUrl);
        return readUrl;
    }

    public static String getReadUser() {
        return XianConfig.get(READONLY_DB_USER);
    }

    public static String getReadPwd() {
        return XianConfig.get(READONLY_DB_PWD);
    }

    public static int getReadPoolSize() {
        return XianConfig.getIntValue(READONLY_DB_POOL_SIZE);
    }

    /**
     * Read configuration from xian config to get the global transaction timeout in milliseconds.
     * The configuration key is "db_transaction_timeout"
     *
     * @return global transaction timeout in milliseconds.
     */
    public static long getTransactionTimeout() {
        return XianConfig.getLongValue(DB_TRANSACTION_TIMEOUT, 1000 * 60 * 5);
    }

}
