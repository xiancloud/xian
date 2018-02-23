package info.xiancloud.plugin.message.sender;

import info.xiancloud.plugin.LocalUnitsManager;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.distribution.UnitJudge;
import info.xiancloud.plugin.distribution.exception.UnitOfflineException;
import info.xiancloud.plugin.distribution.exception.UnitUndefinedException;
import info.xiancloud.plugin.message.RequestContext;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.sender.broadcast.BroadcastSender;
import info.xiancloud.plugin.message.sender.local.DefaultLocalAsyncSender;
import info.xiancloud.plugin.message.sender.remote.RemoteSender;
import info.xiancloud.plugin.message.sender.transfer.TransferSender;
import info.xiancloud.plugin.message.sender.xhash.XhashSender;
import info.xiancloud.plugin.support.mq.mqtt.handle.NotifyHandler;
import info.xiancloud.plugin.util.EnvUtil;

/**
 * @author happyyangyuan
 */
public class SenderFactory {

    public static IAsyncSender getSender(UnitRequest request, NotifyHandler handler) {
        if (EnvUtil.isIDE()) {
            return new DefaultLocalAsyncSender(request, handler);
        }
        String group = request.getContext().getGroup(),
                unit = request.getContext().getUnit();
        if (!UnitJudge.defined(group, unit))
            return new ExceptionHandlerSender(request, handler, new UnitUndefinedException(group, unit));
        if (isTransferable(request.getContext())) {
            //有两种实现方案：
            //1、只在unit不在线时将消息发给中转队列，有点是性能较好，消息不全部过队列中转；缺点是当中转队列有堆积同时unit上线时，会出现乱序。
            //2、这里默认的方案，直接将所有消息经过中转队列，缺点是性能不如方案1，但是绝对不会出现乱序。
            return new TransferSender(request, handler);
        }
        if (!UnitJudge.available(group, unit))
            return new ExceptionHandlerSender(request, handler, new UnitOfflineException(Unit.fullName(group, unit)));
        if (UnitJudge.isBroadcast(group, unit)) {
            return new BroadcastSender(request, handler);
        }
        if (UnitJudge.isXhash(group, unit)) {
            return new XhashSender(request, handler);
        }
        if (LocalUnitsManager.getLocalUnit(Unit.fullName(group, unit)) == null) {
            return new RemoteSender(request, handler);
        } else {
            return new DefaultLocalAsyncSender(request, handler);
        }
    }

    private static boolean isTransferable(RequestContext context) {
        return !context.isTransferredAlready() && UnitJudge.isTransferable(context.getGroup(), context.getUnit());
    }

}
