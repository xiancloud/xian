package info.xiancloud.plugin.dao.core.jdbc.transaction.distributed;

import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.UnitMeta;
import info.xiancloud.plugin.dao.core.group.DaoGroup;
import info.xiancloud.plugin.dao.core.jdbc.transaction.TransactionFactory;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.thread.MsgIdHolder;

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
            return UnitResponse.success();
        } catch (Throwable e) {
            LOG.error(e);
            return UnitResponse.error(DaoGroup.CODE_DB_ERROR, e, "db error");
        }
    }

    @Override
    public Group getGroup() {
        return DaoGroup.singleton;
    }
}
