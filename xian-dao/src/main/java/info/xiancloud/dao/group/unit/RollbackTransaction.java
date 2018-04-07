package info.xiancloud.dao.group.unit;

import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.thread.MsgIdHolder;
import info.xiancloud.dao.group.DaoGroup;
import info.xiancloud.dao.jdbc.transaction.distributed.AppTransaction;

/**
 * roll back the application level transaction.
 *
 * @author happyyangyuan
 * @deprecated todo please implement the abstraction of xian-core's RollbackTransaction interface.
 */
public final class RollbackTransaction implements Unit {

    @Override
    public String getName() {
        return "rollbackTrans";
    }

    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("回滚当前事务并关闭数据库连接;你可以放心调用它任意次,本质上它只执行一次回滚操作,后续调用都是直接被忽略掉的;如果当前不存在事务,那么返回操作失败.").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input();
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        if (AppTransaction.getExistedAppTrans(MsgIdHolder.get()) == null) {
            handler.handle(UnitResponse.createError(DaoGroup.CODE_UNKNOWN_ERROR, MsgIdHolder.get(), String.format("id=%s的事务不存在!", MsgIdHolder.get())));
            return;
        }
        AppTransaction.getExistedAppTrans(MsgIdHolder.get()).rollback();
        AppTransaction.getExistedAppTrans(MsgIdHolder.get()).close();//关闭数据库连接
        handler.handle(UnitResponse.createSuccess("Rollback transaction OK! transId=   " + MsgIdHolder.get()));
        return;
    }

    @Override
    public Group getGroup() {
        return DaoGroup.singleton;
    }
}
