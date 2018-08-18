package info.xiancloud.dao.core.transaction;

import info.xiancloud.core.util.LOG;
import info.xiancloud.dao.core.connection.XianConnection;
import io.reactivex.Completable;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * reentrant transaction abstraction
 *
 * @author happyyangyuan
 */
public abstract class ReentrantTransaction extends BaseXianTransaction {

    protected AtomicInteger count = new AtomicInteger(0);

    public ReentrantTransaction(String transactionId, XianConnection xianConnection) {
        super(transactionId, xianConnection);
    }

    @Override
    public Completable begin() {
        setBegun(true);
        Completable completable;
        if (count.get() == 0) {
            completable = doBegin();
        } else {
            completable = Completable.complete();
        }
        return completable.doOnComplete(this::increaseCount);
    }

    private void increaseCount() {
        count.incrementAndGet();
    }

    @Override
    public Completable rollback() {
        return doRollback().andThen(clear());
    }

    /**
     * Clear this transaction.
     * Clearing transaction does the following steps:<br/>
     * 1. clear cache   <br/>
     * 2. close the connection  <br/>
     * 3. set counter to 0.
     * <p>
     * This method must be implemented to be reentrant to make calling this method more than once to be safe .
     * </p>
     *
     * @return deferred result
     */
    private Completable clear() {
        return Completable.fromAction(() -> count.set(0))
                .andThen(getConnection().close())
                .andThen(doClear());
    }

    /**
     * do custom transaction clear
     *
     * @return deferred result
     */
    protected abstract Completable doClear();

    @Override
    public Completable commit() {
        Completable completable;
        count.decrementAndGet();
        if (count.intValue() == 0) {
            if (connection != null && !connection.isClosed()) {
                completable = doCommit().andThen(clear());
            } else {
                LOG.error("database connection is already closed while you are commit a transaction.");
                completable = clear();
            }
        } else {
            LOG.debug(String.format("嵌套的数据库事务不需要提交,嵌套了%s层", count));
            completable = Completable.complete();
        }
        return completable;
    }

    /**
     * @deprecated transaction has no close operation
     */
    public Completable close() {
///close method is not used anymore.
//        Completable completable;
//        if (count.intValue() == 0) {
//            try {
//                if (connection != null && !connection.isClosed()) {
//                    completable = doClose();
//                } else {
//                    LOG.warn("No connection for you to close");
//                    completable = Completable.complete();
//                }
//            } catch (Throwable e) {
//                LOG.error("关闭数据库连接时发生错误", e);
//                completable = Completable.error(e);
//            } finally {
//                clear();
//            }
//        } else {
//            LOG.warn(new RuntimeException("外层事务还存在,现在还不是关闭数据库连接的时候...,如果想强制关闭数据库连接,请先回滚/提交事务..."));
//            completable = Completable.complete();
//        }
//        return completable;
        return Completable.fromAction(() -> LOG.warn("transaction close method now does nothing."));
    }

    /**
     * Do begin this traction
     *
     * @return deferred transaction beginning result
     */
    protected abstract Completable doBegin();

    /**
     * do commit this transaction
     *
     * @return deferred transaction commitment result
     */
    protected abstract Completable doCommit();

    /**
     * do rollback this transaction
     *
     * @return deferred rollback result
     */
    protected abstract Completable doRollback();

    /*protected abstract Completable doClose();*/

}
