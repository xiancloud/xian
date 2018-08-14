package info.xiancloud.dao.core.transaction;

import info.xiancloud.dao.core.pool.PoolFactory;
import info.xiancloud.dao.core.transaction.local.BaseLocalTransaction;
import io.reactivex.Single;

/**
 * transaction factory
 *
 * @author happyyangyuan
 */
public final class TransactionFactory {

    /**
     * @param transactionId transaction id
     * @param readOnly      boolean flag indicates that whether this dao operation is read-only
     * @return the current transaction reference
     */
    public static Single<XianTransaction> getTransaction(String transactionId, boolean readOnly) {
        if (BaseLocalTransaction.getExistedLocalTrans(transactionId) != null) {
            return Single.just(BaseLocalTransaction.getExistedLocalTrans(transactionId));
        }
        if (readOnly) {
            return PoolFactory.getPool().getSlaveDatasource().getConnection()
                    .map(connection -> connection.createTransaction(transactionId));
        } else {
            return PoolFactory.getPool().getMasterDatasource().getConnection()
                    .map(connection -> connection.createTransaction(transactionId));
        }

    }

    /**
     * @param transactionId transaction id
     * @return the current transaction reference
     */
    public static Single<XianTransaction> getTransaction(String transactionId) {
        return getTransaction(transactionId, false);
    }

}
