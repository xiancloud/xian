package info.xiancloud.plugin.dao.core.jdbc.pool;

import info.xiancloud.plugin.conf.EnvConfig;
import info.xiancloud.plugin.util.LOG;

/**
 * Datasource configuration reader
 *
 * @author happyyangyuan
 */
public class DatasourceConfigReader {

    public static String getWriteUrl() {
        String writeUrl = EnvConfig.get("db_url");
        LOG.info("db_write = " + writeUrl);
        return writeUrl;
    }

    public static String getWritePwd() {
        return EnvConfig.get("db_pwd");
    }

    public static String getWriteUser() {
        return EnvConfig.get("db_user");
    }

    public static int getWritePoolSize() {
        return EnvConfig.getIntValue("db_pool_size");
    }

    public static String getReadUrl() {
        String readUrl = EnvConfig.get("readonly_db_url");
        LOG.info("db_read = " + readUrl);
        return readUrl;
    }

    public static String getReadUser() {
        return EnvConfig.get("readonly_db_user");
    }

    public static String getReadPwd() {
        return EnvConfig.get("readonly_db_pwd");
    }

    public static int getReadPoolSize() {
        return EnvConfig.getIntValue("readonly_db_pool_size");
    }

    public static long getTransactionTimeout() {
        return EnvConfig.getLongValue("db_transaction_timeout", 1000 * 60 * 5);
    }

    public static void main(String... args) {
        LOG.info(getReadPwd());
        LOG.info(getWriteUrl());
        LOG.error(getTransactionTimeout());
    }

}
