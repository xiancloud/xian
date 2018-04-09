package info.xiancloud.dao.jdbc.transaction.distributed;

import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.thread.MsgIdHolder;
import info.xiancloud.dao.group.DaoGroup;
import info.xiancloud.dao.jdbc.transaction.TransactionFactory;

/**
 * @author happyyangyuan
 */
public class CommitAndCloseTogetherUnit implements Unit {
    @Override
    public String getName() {
        return "commitAndCloseTogether";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("分布式事务提交").setDocApi(false);
    }

    @Override
    public Input getInput() {
        return new Input();
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        try {
            IDistributedTransaction distributedTransaction = (IDistributedTransaction) TransactionFactory.getTransaction(MsgIdHolder.get());
            distributedTransaction.commitAndCloseTogether();
            handler.handle(UnitResponse.createSuccess());
            return;
        } catch (Throwable e) {
            LOG.error(e);
            handler.handle(UnitResponse.createError(DaoGroup.CODE_DB_ERROR, e, "db error"));
            return;
        }
    }

    @Override
    public Group getGroup() {
        return DaoGroup.singleton;
    }
}
