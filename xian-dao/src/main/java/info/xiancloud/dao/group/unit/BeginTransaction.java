package info.xiancloud.dao.group.unit;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.thread.MsgIdHolder;
import info.xiancloud.dao.group.DaoGroup;
import info.xiancloud.dao.jdbc.transaction.distributed.AppTransaction;

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
