package info.xiancloud.dao.jdbc.transaction.distributed;

import info.xiancloud.core.*;
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
        return UnitMeta.createWithDescription("分布式事务回滚").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input();
    }

    @Override
    @SuppressWarnings("all")
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        try {
            IDistributedTransaction distributedTransaction = (IDistributedTransaction) TransactionFactory.getTransaction(MsgIdHolder.get());
            distributedTransaction.rollbackAndCloseTogether();
            handler.handle(UnitResponse.createSuccess());
            return;
        } catch (SQLException e) {
            LOG.error(e);
            handler.handle(UnitResponse.createError(DaoGroup.CODE_SQL_ERROR, e, "SQL语句异常"));
            return;
        }
    }

    @Override
    public Group getGroup() {
        return DaoGroup.singleton;
    }
}
