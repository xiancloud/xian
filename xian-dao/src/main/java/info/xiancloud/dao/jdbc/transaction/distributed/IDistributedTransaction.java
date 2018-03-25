package info.xiancloud.dao.jdbc.transaction.distributed;

import java.sql.SQLException;

/**
 * @author happyyangyuan
 */
public interface IDistributedTransaction {
    /**
     * 分布式事务,同时提交和关闭连接
     *
     * @throws SQLException
     */
    void commitAndCloseTogether() throws SQLException;

    /**
     * 分布式事务,同时回滚和关闭连接
     *
     * @throws SQLException
     */
    void rollbackAndCloseTogether() throws SQLException;
}
