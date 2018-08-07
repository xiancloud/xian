package info.xiancloud.dao.jdbc.transaction;

import info.xiancloud.core.init.IStartService;
import info.xiancloud.core.util.LOG;
import info.xiancloud.dao.jdbc.transaction.distributed.AppTransaction;
import info.xiancloud.dao.jdbc.pool.DatasourceConfigReader;

/**
 * 事务超时守护线程
 *
 * @author happyyangyuan
 */
public final class TransactionTimeout implements IStartService {

    private long timeout = DatasourceConfigReader.getTransactionTimeout();

    //单例,懒加载
    private static Thread thread;
    //记录超时线程启动状态,初始值是未启动,触发启动后,会更新该值为"启动"
    private static boolean started = false;

    private Thread getDaemon() {
        if (thread == null) {
            thread = new Thread(() -> {
                while (true) {
                    AppTransaction.getLocalTransMap().values().forEach(this::rollbackIfTimeRunsOutAndClose);
                    try {
                        Thread.sleep(1000);
                        LOG.debug("定时执行事务超时检查...timeout=" + timeout);
                    } catch (InterruptedException e) {
                        LOG.error("", e);
                    }
                }
            });
        }
        return thread;
    }

    /**
     * 回滚并关闭数据库连接,同时删除缓存
     */
    private void rollbackIfTimeRunsOutAndClose(AppTransaction appTransaction) {
        long interval = System.currentTimeMillis() - appTransaction.getCreateDate().getTime();
        if (interval > timeout) {
            LOG.error("发现超时事务:" + appTransaction.toString());
            appTransaction.rollback();
            appTransaction.close();//事务关闭,会同时关闭connection以及从缓存中清除那个事务,所以不会引起内存泄露,请放心使用.
        }
    }

    @Override
    public boolean startup() {
        if (!started) {
            //未启动才启动,如果已经启动了,那么不做任何操作
            getDaemon().start();
            started = true;
        }
        return true;
    }

}
