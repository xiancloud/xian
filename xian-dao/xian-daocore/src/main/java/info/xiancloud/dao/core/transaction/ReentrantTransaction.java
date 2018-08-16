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
     * set count to 0.
     * clear local transaction cache.
     * return the connection to the pool.
     *
     * @return deferred result
     */
    protected abstract Completable clear();

    @Override
    public Completable commit() {
        Completable completable;
        count.decrementAndGet();
        if (count.intValue() == 0) {
            if (connection != null && !connection.isClosed()) {
                completable = doCommit().concatWith(clear());
            } else {
                completable = Completable.error(new RuntimeException("database connection is already closed while you are commit a transaction."))
                        .concatWith(clear());
            }
        } else {
            LOG.debug(String.format("嵌套的数据库事务不需要提交,嵌套了%s层", count));
            completable = Completable.complete();
        }
        return completable;
    }

    /*@Override
    public Completable close() {
        Completable completable;
        if (count.intValue() == 0) {
            try {
                if (connection != null && !connection.isClosed()) {
                    completable = doClose();
                } else {
                    LOG.warn("No connection for you to close");
                    completable = Completable.complete();
                }
            } catch (Throwable e) {
                LOG.error("关闭数据库连接时发生错误", e);
                completable = Completable.error(e);
            } finally {
                clear();
            }
        } else {
            LOG.warn(new RuntimeException("外层事务还存在,现在还不是关闭数据库连接的时候...,如果想强制关闭数据库连接,请先回滚/提交事务..."));
            completable = Completable.complete();
        }
        return completable;
    }*/

    /**
     * do begin this tranction
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
