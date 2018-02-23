package info.xiancloud.plugin.dao.core.jdbc.transaction;

import info.xiancloud.plugin.dao.core.jdbc.transaction.distributed.AppTransaction;
import info.xiancloud.plugin.util.LOG;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 事务工厂
 *
 * @author happyyangyuan
 */
public final class TransactionFactory {

    /**
     * @param readOnly 是否只读
     * @return 获取已存在的事务, 或者新建一个事务
     */
    public static Transaction getTransaction(Object transactionId, boolean readOnly) {
        AppTransaction appTransaction = AppTransaction.getExistedAppTrans(transactionId);
        if (appTransaction == null) {
            if (NoneAppTransaction.getExistedTrans() == null && readOnly) {
                Transaction readOnlyTrans = new ReadOnlyTransaction();
                LOG.debug("创建了一个只读事务,transId = " + readOnlyTrans.getTransactionId());
                return readOnlyTrans;
            }
            Transaction noneAppTrans = NoneAppTransaction.createNoneAppTrans();
            LOG.debug("创建了一个单进程级事务,transId = " + noneAppTrans.getTransactionId());
            return noneAppTrans;
        }
        LOG.debug("已存在应用层事务,直接返回该事务,transId = " + appTransaction.getTransactionId());
        return appTransaction;
    }

    /**
     * @param transId 事务id,使用MsgIdHolder.get()来获取
     * @return 事务对象
     */
    public static Transaction getTransaction(Object transId) {
        return getTransaction(transId, false);
    }


    /**
     * @deprecated 未启用 未测试
     */
    public static Transaction getTransaction(String url, String user, String pwd, Object transactionId, boolean readOnly) {
        //TODO 暂时没需求驱动
        return null;
    }


    /**
     * 构造一个无连接池的数据库事务,使用该方法的目的主要是让被系统可以连接外置的数据源,从而可以对外部数据源执行一些简单的数据库操作.
     * 比如奥付中升级测试和正式环境的数据库
     *
     * @param user 数据库用户名
     * @param pwd  数据库密码
     * @return 一个简单的数据库事务 ,注意:这个事务内的数据库连接是没有连接池支持的.如果数据库拒绝连接,那么返回null
     * @deprecated 未启用 未验证,请谨慎使用
     */
    public static Transaction getTransactionNoPooled(String host, Integer port, String dbName, String user, String pwd) {
        port = port == null ? 3306 : port;
        String url = String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf8", host, port, dbName);
        try {
            Connection conn = DriverManager.getConnection(url, url, pwd);
            return new Transaction(conn, "url" + "||" + user) {
                @Override
                public Transaction begin() {
                    try {
                        conn.setAutoCommit(false);
                    } catch (SQLException e) {
                        LOG.error(e);
                    }
                    return this;
                }

                @Override
                public Transaction commit() {
                    try {
                        conn.commit();
                    } catch (SQLException e) {
                        LOG.error(e);
                    }
                    return this;
                }

                @Override
                public Transaction rollback() {
                    try {
                        conn.rollback();
                    } catch (SQLException e) {
                        LOG.error(e);
                    }
                    return this;
                }

                @Override
                public void close() {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        LOG.error(e);
                    }
                }
            };
        } catch (Throwable ex) {
            LOG.error(ex);
            return null;
        }
    }

}
