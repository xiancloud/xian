package info.xiancloud.plugin.message.sender.transfer;

import info.xiancloud.plugin.distribution.LocalNodeManager;
import info.xiancloud.plugin.mq.TransferQueueUtil;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.sender.AbstractAsyncSender;
import info.xiancloud.plugin.support.mq.mqtt.handle.NotifyHandler;
import info.xiancloud.plugin.util.LOG;

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
