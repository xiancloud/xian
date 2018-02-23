package info.xiancloud.plugin.dao.core.jdbc.transaction;

import info.xiancloud.plugin.dao.core.jdbc.pool.PoolFactory;
import info.xiancloud.plugin.util.LOG;

/**
 * 非应用层事务（单进程级别内的事务,可解决db接口调用接口db时保证其是在一个事务内）
 *
 * @author happyyangyuan
 * @deprecated 原本是可以使用它解决db接口内部调用另一些服务接口/db接口时默认使用一个事务的场景的,现在看来也用不上,因为我们在db接口内根本不会调用任何其他unit接口
 */
public class NoneAppTransaction extends JoinableTransaction {

    private static ThreadLocal<NoneAppTransaction> threadConnection = new InheritableThreadLocal<>();

    /**
     * 获取已存在的db进程级事务,如果不存在那么返回null
     */
    public static NoneAppTransaction getExistedTrans() {
        return threadConnection.get();
    }

    public static NoneAppTransaction createNoneAppTrans() {
        if (getExistedTrans() == null) {
            NoneAppTransaction trans = new NoneAppTransaction();
            threadConnection.set(trans);
            return trans;
        }
        LOG.info("Transaction is already begun. Joined to existed transaction only. transactionId=" + threadConnection.get().getTransactionId());
        return getExistedTrans();
    }

    private NoneAppTransaction() {
        super(PoolFactory.getPool().getWriteConnection(), "NoneAppTransaction");
    }

    @Override
    protected void clear() {
        threadConnection.set(null);
    }

}
