package info.xiancloud.dao.async.postgresql;

import info.xiancloud.dao.core.connection.BaseXianConnection;
import info.xiancloud.dao.core.connection.XianConnection;
import io.reactivex.Completable;

/**
 * Async non-transactional postgreSQL connection implementation for {@link XianConnection}
 *
 * @author happyyangyuan
 */
public class PgConnection extends BaseXianConnection {

    /**
     * postgresql rxJava2 style connection reference.
     */
    private io.reactiverse.reactivex.pgclient.PgConnection pgConnection0;

    public PgConnection setPgConnection0(io.reactiverse.reactivex.pgclient.PgConnection pgConnection0) {
        this.pgConnection0 = pgConnection0;
        pgConnection0.closeHandler(event -> closed = true);
        return this;
    }

    public io.reactiverse.reactivex.pgclient.PgConnection getPgConnection0() {
        return pgConnection0;
    }


    @Override
    protected Completable doClose() {
        return Completable.fromAction(() -> pgConnection0.close());
    }

}
