package info.xiancloud.plugin.dao.core.group.unit;

import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.UnitMeta;
import info.xiancloud.plugin.dao.core.group.DaoGroup;
import info.xiancloud.plugin.dao.core.jdbc.transaction.distributed.AppTransaction;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.util.thread.MsgIdHolder;

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
            return UnitResponse.error(DaoGroup.CODE_OPERATE_ERROR, MsgIdHolder.get(), String.format("id=%s的事务不存在!", MsgIdHolder.get()));
        } else {
            AppTransaction.getExistedAppTrans(MsgIdHolder.get()).commit();
            AppTransaction.getExistedAppTrans(MsgIdHolder.get()).close();//关闭数据库连接
            return UnitResponse.success("Commit Transaction OK! transId=   " + MsgIdHolder.get());
        }
    }

    @Override
    public Group getGroup() {
        //todo
        return null;
    }
}
