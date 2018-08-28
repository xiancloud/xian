package info.xiancloud.dao.jdbc.transaction;

import info.xiancloud.dao.core.connection.XianConnection;
import info.xiancloud.dao.core.transaction.local.BaseLocalTransaction;
import info.xiancloud.dao.jdbc.connection.JdbcConnection;
import io.reactivex.Completable;

/**
 * jdbc local transaction implementation
 *
 * @author happyyangyuan
 */
public class JdbcLocalTransaction extends BaseLocalTransaction {

    private java.sql.Connection connection0;

    public JdbcLocalTransaction(String transactionId, XianConnection xianConnection) {
        super(transactionId, xianConnection);
        this.connection0 = ((JdbcConnection) xianConnection).getConnection0();
    }

    @Override
    protected Completable doBegin() {
        return Completable.fromAction(() -> {
            if (connection0.getAutoCommit()) {
                connection0.setAutoCommit(false);
            }
        });
    }

    @Override
    protected Completable doCommit() {
        return Completable.fromAction(() -> connection0.commit());
    }

    @Override
    protected Completable doRollback() {
        return Completable.fromAction(() -> {
            connection0.rollback();
        });
    }
}
