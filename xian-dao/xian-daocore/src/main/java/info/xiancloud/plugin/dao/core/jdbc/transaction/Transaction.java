package info.xiancloud.plugin.dao.core.jdbc.transaction;

import java.sql.Connection;

/**
 * 脱离spring做事务
 *
 * @author happyyangyuan
 */
public abstract class Transaction {

    protected Connection connection;
    protected boolean rollbacked = false;
    protected Object transactionId;

    protected Transaction(Connection connection, Object transactionId) {
        this.connection = connection;
        this.transactionId = transactionId;
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * 开始事务,本质是将数据库连接设置为autoCommit=false
     */
    public abstract Transaction begin();

    public abstract Transaction commit();

    public abstract Transaction rollback();

    /**
     * 关闭数据库连接(如果使用数据库连接池,那么返还给数据库连接池),然后事务对象变为无效
     */
    public abstract void close();

    public Object getTransactionId() {
        return transactionId;
    }

}
