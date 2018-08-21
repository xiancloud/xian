package info.xiancloud.dao.core.transaction;

import info.xiancloud.core.util.thread.MsgIdHolder;
import info.xiancloud.dao.core.connection.XianConnection;
import info.xiancloud.dao.core.pool.PoolFactory;
import io.reactivex.Completable;

import java.util.Date;

/**
 * Transaction interface in xian frame.
 * <p>
 * You must know the following:
 * 0. {@link PoolFactory pool factory} produces connection pool which both writable and read-only datasource are contained.
 * 1. database connection pool produces connections.
 * 2. connection begins(produces) transactions.
 * 3. transaction commits or rollbacks.
 * 4. transaction holds its connection reference.
 * 5. connection does not own the transaction reference.
 * 6. xian dao should not rely on the {@link MsgIdHolder} ability.
 * 7. transactions has a close method which returns connection to the pool and clear every thing associated with this transaction.
 * 8. transaction needs to be closed right after it is rollbacked or committed, and connection it references is returned to pool immediately.
 *
 * @author happyyangyuan
 * @see info.xiancloud.dao.core.pool.IPool
 * @see XianConnection
 */
public interface XianTransaction {

    /**
     * getter for this transaction's connection reference
     *
     * @return the connection reference owned by this transaction
     */
    XianConnection getConnection();

    /**
     * begin this transaction.
     *
     * @return deferred result
     */
    Completable begin();

    /**
     * Submit a commit request.
     * Note that the transaction will only be committed until all nested transaction commitment requests are submitted.
     * Note that transaction needs to be closed and be cleared later by yourself.
     *
     * @return Represents a deferred commit result without any value but only indication for completion or exception.
     */
    Completable commit();

    /**
     * Submit a rollback request immediately to the database.
     * Note that transaction needs to be closed and be cleared later by yourself.
     *
     * @return Represents a deferred rollback result without any value but only indication for completion or exception.
     */
    Completable rollback();

    /**
     * get the transaction
     *
     * @return transaction id
     */
    String getTransactionId();

    /**
     * Get transaction creation date.
     *
     * @return transaction creation date.
     */
    Date getCreateDate();

    /**
     * whether or not this transaction is begun
     *
     * @return true or false
     */
    boolean isBegun();

    /**
     * close this transaction and clear all resources associated with it.
     * This method is reentrant, you can call it more than one time.
     *
     * @return deferred result
     */
    Completable close();

}
