package info.xiancloud.core.util;

import info.xiancloud.core.message.SingleRxXian;
import io.reactivex.Completable;

/**
 * Utility for transaction commitment and rollback
 */
public class TransactionUtil {


    /**
     * begin the give transaction
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
     * commit the give transaction
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
     * rollback the transaction in current context
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
