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
        return UnitMeta.create("分布式事务提交").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input();
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        try {
            IDistributedTransaction distributedTransaction = (IDistributedTransaction) TransactionFactory.getTransaction(MsgIdHolder.get());
            distributedTransaction.commitAndCloseTogether();
            return UnitResponse.createSuccess();
        } catch (Throwable e) {
            LOG.error(e);
            return UnitResponse.createError(DaoGroup.CODE_DB_ERROR, e, "db error");
        }
    }

    @Override
    public Group getGroup() {
        return DaoGroup.singleton;
    }
}
