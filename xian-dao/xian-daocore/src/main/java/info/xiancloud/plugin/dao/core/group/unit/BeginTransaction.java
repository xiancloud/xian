package info.xiancloud.plugin.dao.core.group.unit;

import info.xiancloud.plugin.dao.core.jdbc.transaction.distributed.AppTransaction;
import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.dao.core.group.DaoGroup;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.thread.MsgIdHolder;

/**
 * 请不要修改此类
 *
 * @author happyyangyuan
 */
public final class BeginTransaction implements Unit {
    @Override
    public String getName() {
        return "beginTrans";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("开始事务，返回事务id").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input();
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        LOG.info(String.format("事务开始...transId=%s", MsgIdHolder.get()));
        AppTransaction.createTransaction(MsgIdHolder.get()).begin();
        return UnitResponse.success("Begin Transaction OK! transId=  " + MsgIdHolder.get());
    }

    @Override
    public Group getGroup() {
        return DaoGroup.singleton;
    }
}
