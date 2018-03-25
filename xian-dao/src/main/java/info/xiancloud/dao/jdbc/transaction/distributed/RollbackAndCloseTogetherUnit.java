package info.xiancloud.dao.jdbc.transaction.distributed;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.thread.MsgIdHolder;
import info.xiancloud.dao.group.DaoGroup;
import info.xiancloud.dao.jdbc.transaction.TransactionFactory;

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
