package info.xiancloud.dao.core.units;

import info.xiancloud.core.Handler;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.thread.MsgIdHolder;
import info.xiancloud.dao.core.DaoGroup;
import info.xiancloud.dao.core.transaction.local.BaseLocalTransaction;

/**
 * Commit the transaction in the context.
 *
 * @author happyyangyuan
 */
public abstract class CommitTransaction implements Unit {

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("提交事务").setDocApi(false);
    }

    @Override
    public Input getInput() {
        return new Input();
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        if (BaseLocalTransaction.getExistedLocalTrans(MsgIdHolder.get()) == null) {
            handler.handle(UnitResponse.createError(DaoGroup.CODE_UNKNOWN_ERROR, MsgIdHolder.get(), String.format("Transaction with id=%s does not exist.", MsgIdHolder.get())));
        } else {
            BaseLocalTransaction.getExistedLocalTrans(MsgIdHolder.get())
                    .commit()
                    .subscribe(() -> handler.handle(UnitResponse.createSuccess("Commit Transaction OK! transId=   " + MsgIdHolder.get())));
        }
    }

}
