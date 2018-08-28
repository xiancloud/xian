package info.xiancloud.dao.async.postgresql;

import info.xiancloud.dao.core.connection.XianConnection;
import info.xiancloud.dao.core.transaction.local.BaseLocalTransaction;
import io.reactiverse.reactivex.pgclient.PgTransaction;
import io.reactivex.Completable;

/**
 * postgresql non-blocking transaction
 *
 * @author happyyangyuan
 */
public class PgLocalTransaction extends BaseLocalTransaction {

    private PgTransaction pgTransaction0;

    public PgLocalTransaction(String transactionId, XianConnection xianConnection) {
        super(transactionId, xianConnection);
    }

    @Override
    protected Completable doBegin() {
        return Completable.fromAction(() -> {
            PgConnection pgConnection = (PgConnection) connection;
            io.reactiverse.reactivex.pgclient.PgConnection pgConnection0 = pgConnection.getPgConnection0();
            pgTransaction0 = pgConnection0.begin();
        });
    }

    @Override
    protected Completable doCommit() {
        return pgTransaction0.rxCommit();
    }

    @Override
    protected Completable doRollback() {
        return pgTransaction0.rxRollback();

        ///fixme, seems that reactive-pg-client's rxRollback() and rollback(handler) both have some bug.
        /*return Completable.create(emitter -> {
            pgTransaction0.rollback(event -> {
                if (event.succeeded()) {
                    emitter.onComplete();
                } else {
                    emitter.onError(event.cause());
                }
            });
        });*/
    }


}
