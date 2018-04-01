package info.xiancloud.dao.group.unit;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.thread.MsgIdHolder;
import info.xiancloud.dao.group.DaoGroup;
import info.xiancloud.dao.jdbc.transaction.distributed.AppTransaction;

/**
 * 请不要修改此类
 *
 * @deprecated todo implements interface of xian-core instead of defining a unit.
 */
public final class CommitTransaction implements Unit {
    @Override
    public String getName() {
        return "commitTrans";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("提交事务").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input();
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        if (AppTransaction.getExistedAppTrans(MsgIdHolder.get()) == null) {
            return UnitResponse.createError(DaoGroup.CODE_OPERATE_ERROR, MsgIdHolder.get(), String.format("id=%s的事务不存在!", MsgIdHolder.get()));
        } else {
            AppTransaction.getExistedAppTrans(MsgIdHolder.get()).commit();
            AppTransaction.getExistedAppTrans(MsgIdHolder.get()).close();//关闭数据库连接
            return UnitResponse.createSuccess("Commit Transaction OK! transId=   " + MsgIdHolder.get());
        }
    }

    @Override
    public Group getGroup() {
        //todo
        return null;
    }
}
