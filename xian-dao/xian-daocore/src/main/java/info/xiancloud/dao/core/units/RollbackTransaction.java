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
 * Roll back the application level transaction.
 *
 * @author happyyangyuan
 */
public abstract class RollbackTransaction implements Unit {

    @Override
    public String getName() {
        return "rollbackTrans";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta
                .createWithDescription("回滚当前事务并关闭数据库连接;你可以放心调用它任意次,本质上它只执行一次回滚操作,后续调用都是直接被忽略掉的;如果当前不存在事务,那么返回操作失败.")
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
            handler.handle(UnitResponse.createError(DaoGroup.CODE_UNKNOWN_ERROR, MsgIdHolder.get(), String.format("Transaction with id=%s does not exist.", MsgIdHolder.get())));
        } else {
            transaction
                    .rollback()
                    //close the transaction asynchronously is ok and better
                    .doFinally(() -> transaction.close().subscribe())
                    .subscribe(() -> handler.handle(UnitResponse.createSuccess("Rollback transaction OK! transId=   " + MsgIdHolder.get())));
        }
    }

}
