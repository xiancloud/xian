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
        PgConnection pgConnection = (PgConnection) connection;
        io.reactiverse.reactivex.pgclient.PgConnection pgConnection0 = pgConnection.getPgConnection0();
        pgTransaction0 = pgConnection0.begin();
        return Completable.complete();
    }

    @Override
    protected Completable doCommit() {
        return pgTransaction0.rxCommit();
    }

    @Override
    protected Completable doRollback() {
        return pgTransaction0.rxRollback();
    }

}
