package info.xiancloud.dao.core.pool;

import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.util.LOG;

/**
 * Datasource configuration reader. Datasource configuration property file demo: <br/>
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
     * read master datasource configuration from xian config.
     * please refer to {@link XianDataSource#url}  to see database configuration format
     *
     * @return datasource configuration url
     */
    public static String getWriteUrl() {
        String writeUrl = XianConfig.get("db_url");
        LOG.info("db_url = " + writeUrl);
        return writeUrl;
    }

    public static String getWriteUser() {
        return XianConfig.get("db_user");
    }

    public static String getWritePwd() {
        return XianConfig.get("db_pwd");
    }

    public static int getWritePoolSize() {
        return XianConfig.getIntValue("db_pool_size");
    }

    /**
     * read slave datasource configuration from xian config.
     * please refer to {@link XianDataSource#url}  to see database configuration format
     *
     * @return datasource configuration url
     */
    public static String getReadUrl() {
        String readUrl = XianConfig.get("readonly_db_url");
        LOG.info("readonly_db_url = " + readUrl);
        return readUrl;
    }

    public static String getReadUser() {
        return XianConfig.get("readonly_db_user");
    }

    public static String getReadPwd() {
        return XianConfig.get("readonly_db_pwd");
    }

    public static int getReadPoolSize() {
        return XianConfig.getIntValue("readonly_db_pool_size");
    }

    /**
     * Read configuration from xian config to get the global transaction timeout in milliseconds.
     * The configuration key is "db_transaction_timeout"
     *
     * @return global transaction timeout in milliseconds.
     */
    public static long getTransactionTimeout() {
        return XianConfig.getLongValue("db_transaction_timeout", 1000 * 60 * 5);
    }

}
