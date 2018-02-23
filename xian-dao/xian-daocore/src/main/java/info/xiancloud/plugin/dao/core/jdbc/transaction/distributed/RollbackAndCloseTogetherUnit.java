package info.xiancloud.plugin.dao.core.jdbc.transaction.distributed;

import info.xiancloud.plugin.dao.core.jdbc.transaction.TransactionFactory;
import info.xiancloud.plugin.*;
import info.xiancloud.plugin.dao.core.group.DaoGroup;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.thread.MsgIdHolder;

import java.sql.SQLException;

/**
 * @author happyyangyuan
 */
public class RollbackAndCloseTogetherUnit implements Unit {
    @Override
    public String getName() {
        return "rollbackAndCloseTogether";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("分布式事务回滚").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input();
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        try {
            IDistributedTransaction distributedTransaction = (IDistributedTransaction) TransactionFactory.getTransaction(MsgIdHolder.get());
            distributedTransaction.rollbackAndCloseTogether();
            return UnitResponse.success();
        } catch (SQLException e) {
            LOG.error(e);
            return UnitResponse.error(DaoGroup.CODE_SQL_ERROR, e, "SQL语句异常");
        }
    }

    @Override
    public Group getGroup() {
        return DaoGroup.singleton;
    }
}
