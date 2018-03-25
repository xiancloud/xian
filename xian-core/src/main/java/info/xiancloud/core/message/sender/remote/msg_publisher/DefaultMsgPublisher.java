package info.xiancloud.core.message.sender.remote.msg_publisher;

import info.xiancloud.core.mq.TransferQueueUtil;
import info.xiancloud.core.mq.IMqPubClient;
import info.xiancloud.core.rpc.RpcClient;
import info.xiancloud.core.rpc.RpcServerStatusManager;
import info.xiancloud.core.util.LOG;

/**
 * 默认的跨节点消息发布器
 *
 * @author happyyangyuan
 */
public class DefaultMsgPublisher implements IMsgPublisher {

    @Override
    public boolean p2pPublish(String nodeId, String payload) {
        if (TransferQueueUtil.isTransferQueue(nodeId)) {
            LOG.debug("中转节点直接发mq消息的");
            return IMqPubClient.singleton.staticPublish(nodeId, payload);
        }
        return RpcClient.singleton.request(nodeId, payload);
    }

    /**
     * @deprecated 不再支持rpc失败则发送mq消息，因为暂时没实现开机启动时订阅自节点消息；
     * 原因：1、rpc已经相当稳定了。
     * 2、所有节点均去连接mq server，对server压力较大，之前也出现过事故：新节点连接rabbitmq失败，重启rabbitmq才解决。
     */
    private boolean compositePublish(String nodeId, String payload) {
        boolean succeeded;
        if (RpcServerStatusManager.canTry(nodeId)) {
            if (!RpcClient.singleton.request(nodeId, payload)) {
                RpcServerStatusManager.updateStatus(nodeId, false);
                succeeded = IMqPubClient.singleton.p2pPublish(nodeId, payload);
            } else {
                succeeded = true;
                RpcServerStatusManager.updateStatus(nodeId, true);
            }
        } else {
            succeeded = IMqPubClient.singleton.p2pPublish(nodeId, payload);
        }
        return succeeded;
    }

}
