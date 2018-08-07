package info.xiancloud.dao.jdbc.transaction;

import info.xiancloud.core.util.LOG;
import info.xiancloud.dao.jdbc.pool.PoolFactory;

import java.sql.SQLException;

/**
 * 为了统一API，这只是封装了一个只读的数据库连接而已
 *
 * @author happyyangyuan
 */
public class ReadOnlyTransaction extends Transaction {

    public ReadOnlyTransaction() {
        super(PoolFactory.getPool().getReadConnection(), "ReadOnlyTransaction");
        try {
            if (!connection.isReadOnly()) {
                connection.setReadOnly(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ReadOnlyTransaction rollback() {
        rollbacked = true;//表明它已经被回滚过,这里"只读事务"只是为了达成跟真正事务保持一致的状态管理而已.
        return this;
    }

    @Override
    public ReadOnlyTransaction begin() {
        return this;
    }

    @Override
    public ReadOnlyTransaction commit() {
        return this;
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                LOG.debug("---------jdbc connection.closed---maybe returned to db pool actually----");
            }
        } catch (SQLException e) {
            LOG.error("关闭数据库连接时发生错误", e);
        }
    }

}
