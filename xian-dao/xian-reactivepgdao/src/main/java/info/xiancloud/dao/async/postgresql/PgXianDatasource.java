package info.xiancloud.dao.async.postgresql;

import info.xiancloud.core.util.LOG;
import info.xiancloud.dao.core.connection.XianConnection;
import info.xiancloud.dao.core.pool.XianDataSource;
import io.reactiverse.pgclient.PgPoolOptions;
import io.reactiverse.reactivex.pgclient.PgClient;
import io.reactiverse.reactivex.pgclient.PgPool;
import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * postgresql xian datasource implementation.
 *
 * @author happyyangyuan
 */
public class PgXianDatasource extends XianDataSource {

    private PgPool pgDatasource;
    private PgPoolOptions datasourceOptions;

    /**
     * this constructor is blocking.
     * You can synchronize this method to avoid multiple datasource creation.
     *
     * @param connectionStr the xian standard connection string. (we use mysql standard connection string as xian standard.)
     * @param username      the username of your database.
     * @param password      the password of your database.
     * @param poolSize      the max pool size.
     */
    public PgXianDatasource(String connectionStr, String username, String password, int poolSize) {
        this.url = connectionStr;
        this.user = username;
        this.pwd = password;
        datasourceOptions = new PgPoolOptions()
                .setPort(getPort())
                .setHost(getHost())
                .setDatabase(getDatabase())
                .setUser(user)
                .setPassword(pwd)
                .setMaxSize(poolSize);
        pgDatasource = PgClient.pool(datasourceOptions);
    }

    @Override
    public Single<XianConnection> getConnection() {
        return pgDatasource
                .rxGetConnection()
                .map(pgConnection -> new PgConnection().setPgConnection0(pgConnection));
    }

    @Override
    public Completable destroy() {
        try {
            pgDatasource.close();
            return Completable.complete();
        } catch (Throwable e) {
            return Completable.error(e);
        }
    }

    @Override
    public int getActiveConnectionCount() {
        throw new RuntimeException("Not supported yet.");
    }

    @Override
    public int getPoolSize() {
        return datasourceOptions.getMaxSize();
    }
}
