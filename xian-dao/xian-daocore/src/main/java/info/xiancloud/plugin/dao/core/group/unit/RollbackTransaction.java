package info.xiancloud.plugin.dao.core.group.unit;

import info.xiancloud.plugin.dao.core.jdbc.transaction.distributed.AppTransaction;
import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.dao.core.group.DaoGroup;
import info.xiancloud.plugin.util.thread.MsgIdHolder;

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
        return UnitMeta.create("回滚当前事务并关闭数据库连接;你可以放心调用它任意次,本质上它只执行一次回滚操作,后续调用都是直接被忽略掉的;如果当前不存在事务,那么返回操作失败.").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input();
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        if (AppTransaction.getExistedAppTrans(MsgIdHolder.get()) == null) {
            return UnitResponse.error(DaoGroup.CODE_OPERATE_ERROR, MsgIdHolder.get(), String.format("id=%s的事务不存在!", MsgIdHolder.get()));
        }
        AppTransaction.getExistedAppTrans(MsgIdHolder.get()).rollback();
        AppTransaction.getExistedAppTrans(MsgIdHolder.get()).close();//关闭数据库连接
        return UnitResponse.success("Rollback transaction OK! transId=   " + MsgIdHolder.get());
    }

    @Override
    public Group getGroup() {
        return DaoGroup.singleton;
    }
}
