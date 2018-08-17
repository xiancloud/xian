package info.xiancloud.dao.core.connection;

import info.xiancloud.dao.core.transaction.XianTransaction;
import io.reactivex.Completable;

/**
 * Interface for database connection
 * Things you must know:
 * 1. a xian connection is held by transaction.
 * 2. a xian connection is shared by multiple sql actions.
 *
 * @author happyyangyuan
 */
public interface XianConnection {
    /**
     * indicate whether the connection has been closed.
     *
     * @return true if closed, false if still open.
     */
    boolean isClosed();

    /**
     * Create a transaction object which is not started yet.
     * Transactions are all produced by connections.
     *
     * @param transactionId transaction id
     * @return created transaction
     */
    XianTransaction createTransaction(String transactionId);

    /**
     * return the connection to the pool.
     * This method must be implemented to be reentrant, we can close a xian connection more than one time. And latter close operation does nothing.
     *
     * @return Represents a deferred computation without any value but only indication for completion or exception.
     */
    Completable close();
}
