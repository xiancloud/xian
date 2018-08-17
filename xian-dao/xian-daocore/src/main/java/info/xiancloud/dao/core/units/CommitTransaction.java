package info.xiancloud.dao.core.units;

import info.xiancloud.core.Handler;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.thread.MsgIdHolder;
import info.xiancloud.dao.core.DaoGroup;
import info.xiancloud.dao.core.transaction.XianTransaction;
import info.xiancloud.dao.core.transaction.local.BaseLocalTransaction;

/**
 * Commit the transaction in the context.
 * Note that transaction commitment must be called sequentially in the "finally" way to make sure it is called no matter any exception is thrown.
 *
 * @author happyyangyuan
 */
public abstract class CommitTransaction implements Unit {

    @Override
    public UnitMeta getMeta() {
        return UnitMeta
                .createWithDescription("Commit current transaction.")
                .appendDescription("If current transaction is already ended then do nothing.")
                .setDocApi(false);
    }

    @Override
    public Input getInput() {
        return new Input();
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        XianTransaction transaction = BaseLocalTransaction.getExistedLocalTrans(MsgIdHolder.get());
        if (transaction == null) {
            handler.handle(UnitResponse.createError(DaoGroup.CODE_TRANSACTION_ALREADY_ENDS, MsgIdHolder.get(),
                    String.format("Transaction with id=%s does not exist.", MsgIdHolder.get())));
        } else {
            transaction
                    .commit()
                    .subscribe(() -> handler.handle(UnitResponse.createSuccess("Commit Transaction OK! transId=   " + MsgIdHolder.get())));
        }
    }

}
