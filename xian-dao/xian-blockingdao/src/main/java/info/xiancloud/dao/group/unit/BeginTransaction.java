package info.xiancloud.dao.group.unit;

import info.xiancloud.core.*;
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
        return UnitMeta.createWithDescription("开始事务，返回事务id").setDocApi(false);
    }

    @Override
    public Input getInput() {
        return new Input();
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        LOG.info(String.format("事务开始...transId=%s", MsgIdHolder.get()));
        AppTransaction.createTransaction(MsgIdHolder.get()).begin();
        handler.handle(UnitResponse.createSuccess("Begin Transaction OK! transId=  " + MsgIdHolder.get()));
    }

    @Override
    public Group getGroup() {
        return DaoGroup.singleton;
    }
}
