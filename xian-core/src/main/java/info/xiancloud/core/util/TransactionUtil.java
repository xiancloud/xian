package info.xiancloud.core.util;

import info.xiancloud.core.message.SingleRxXian;
import io.reactivex.Completable;

/**
 * Utility for transaction commitment and rollback
 *
 * @author happyyangyuan
 */
public class TransactionUtil {

    /**
     * Begin the given dao group's node's transaction. If currently the transaction is not existed then create one.
     * This method is reentrant and must be paired with {@link #commit(String daoGroup)} method.
     *
     * @param daoGroup dao group name. This helps to locate which dao application node to sent this request.
     * @return deferred transaction beginning result
     */
    public static Completable begin(String daoGroup) {
        return SingleRxXian.call(daoGroup, "BeginTransaction").flatMapCompletable(unitResponse -> {
            if (unitResponse.succeeded()) {
                return Completable.complete();
            } else {
                throw new RuntimeException("Begin Transaction failure");
            }
        });
    }

    /**
     * Commit the give transaction.
     * Note that if you have rollbacked the transaction, then commitment of this transaction will do nothing.
     * You don't worry about transaction closing, the frame will clear anything for you after the transaction is commited
     * or exception obscures.
     *
     * @param daoGroup dao group name. This helps to locate which dao application node to sent this request.
     * @return deferred transaction commitment result
     */
    public static Completable commit(String daoGroup) {
        return SingleRxXian.call(daoGroup, "CommitTransaction").flatMapCompletable(unitResponse -> {
            if (unitResponse.succeeded()) {
                return Completable.complete();
            } else {
                throw new RuntimeException("Transaction commit failure");
            }
        });
    }

    /**
     * Rollback the transaction in current context.
     * Note that once a transaction is rollbacked its lifecycle ends.
     * Any later transaction commitment will do nothing.
     *
     * @param daoGroup dao group name. This helps to locate which dao application node to sent this request.
     * @return deferred transaction rollback result
     */
    public static Completable rollback(String daoGroup) {
        return SingleRxXian.call(daoGroup, "RollbackTransaction")
                .flatMapCompletable(unitResponse -> {
                    if (unitResponse.succeeded()) {
                        return Completable.complete();
                    } else {
                        throw new RuntimeException("Transaction rollback failure");
                    }
                });
    }
}
