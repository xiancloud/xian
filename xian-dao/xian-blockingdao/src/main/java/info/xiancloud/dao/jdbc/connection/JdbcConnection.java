package info.xiancloud.dao.jdbc.connection;

import info.xiancloud.dao.core.connection.BaseXianConnection;
import io.reactivex.Completable;

import java.sql.Connection;

/**
 * xian connection jdbc implementation
 *
 * @author happyyangyuan
 */
public class JdbcConnection extends BaseXianConnection {

    /**
     * inner jdbc pooled connection
     */
    private Connection connection0;

    @Override
    public Completable doClose() {
        return Completable.fromAction(() -> connection0.close());
    }

    public void setConnection0(Connection connection0) {
        this.connection0 = connection0;
    }
}
