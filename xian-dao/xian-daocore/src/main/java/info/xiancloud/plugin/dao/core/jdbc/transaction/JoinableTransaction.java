package info.xiancloud.plugin.dao.core.jdbc.transaction;

import info.xiancloud.plugin.util.LOG;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 类似spring的申明式事务，可以加入已有事务中
 *
 * @author happyyangyuan
 */
public abstract class JoinableTransaction extends Transaction {
    protected Integer count;

    protected JoinableTransaction(Connection connection, Object transactionId) {
        super(connection, transactionId);
        count = 0;
    }

    @Override
    public JoinableTransaction begin() {
        if (count == 0) {
            try {
                doBegin();
            } catch (SQLException e) {
                LOG.error(e);
            }
        }
        increaseCount();
        return this;
    }

    private int increaseCount() {
        count++;
        return count;
    }

    public JoinableTransaction rollback() {
        LOG.debug("JoinableTransaction------   正在执行回滚操作");
        try {
            doRollback(); /*connection.rollback();*/
        } catch (SQLException e) {
            LOG.error(e);
        }
        if (rollbacked) {
            LOG.error("事务已经回滚过!又再次执行回滚操作!请谨慎处理!");
        }
        rollbacked = true;
        count = 0;//初始化count值
        LOG.debug("JoinableTransaction------   回滚完成...");
        return this;
    }

    protected abstract void clear();

    public JoinableTransaction commit() {
        count--;
        if (count == 0) {
            try {
                if (connection != null && !connection.isClosed()) {
                    LOG.debug("---------jdbc connection.commit-------");
                    doCommit();/*connection.commit();*/
                    LOG.debug("---------jdbc connection.committed-------");
                } else {
                    LOG.error("数据库连接已经关闭了,如果程序还试图提交事务,请检查是不是程序出现了错误?");
                }
            } catch (SQLException e) {
                LOG.error(e);
            }
        } else {
            LOG.debug(String.format("嵌套的数据库事务不需要提交,嵌套了%s层", count));
        }
        return this;
    }

    public void close() {
        LOG.debug("-----关闭事务...事务控制的计数器count=" + count);
        if (count == 0) {
            try {
                if (connection != null && !connection.isClosed()) {
                    doClose();/*connection.close();*/
                    LOG.debug("---------jdbc connection.closed---maybe returned to db pool actually----");
                }
            } catch (SQLException e) {
                LOG.error("关闭数据库连接时发生错误");
            } finally {
                clear();
            }
        } else {
            LOG.debug("外层事务还存在,现在还不是关闭数据库连接的时候...,如果想强制关闭数据库连接,请先回滚/提交事务...");
        }
    }

    protected void doBegin() throws SQLException {
        if (connection.getAutoCommit()) {
            connection.setAutoCommit(false);
        }
    }

    protected void doCommit() throws SQLException {
        connection.commit();
    }

    protected void doRollback() throws SQLException {
        connection.rollback();
    }

    protected void doClose() throws SQLException {
        connection.close();
    }

}
