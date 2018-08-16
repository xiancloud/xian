package info.xiancloud.dao.core.units;

import info.xiancloud.core.Handler;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.thread.MsgIdHolder;
import info.xiancloud.dao.core.transaction.TransactionFactory;

/**
 * begin transaction in current context.
 * If there is no transaction exists, create one.
 *
 * @author happyyangyuan
 */
public abstract class BeginTransaction implements Unit {

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
        TransactionFactory.getTransaction(MsgIdHolder.get())
                .subscribe(xianTransaction -> xianTransaction.begin()
                        .subscribe(() ->
                                handler.handle(UnitResponse.createSuccess("Begin Transaction OK! transId=  " + MsgIdHolder.get())))
                );
    }

}
