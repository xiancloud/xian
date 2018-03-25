package info.xiancloud.core.message.sender.transfer;

import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.mq.TransferQueueUtil;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.sender.AbstractAsyncSender;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.util.LOG;

/**
 * 中转发送器，针对mqClientNotFound时触发实现堆积。
 *
 * @author happyyangyuan
 */
public class TransferSender extends AbstractAsyncSender {

    public TransferSender(UnitRequest request, NotifyHandler handler) {
        super(request, handler);
    }

    @Override
    protected void asyncSend() {
        String staticQueue = TransferQueueUtil.getTransferQueue(unitRequest.getContext().getGroup());
        LOG.info("send request to static queue: " + staticQueue);
        unitRequest.getContext().setDestinationNodeId(staticQueue);
        LocalNodeManager.send(unitRequest, callback);
    }
}
