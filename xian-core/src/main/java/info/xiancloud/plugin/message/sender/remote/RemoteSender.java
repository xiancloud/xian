package info.xiancloud.plugin.message.sender.remote;

import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.distribution.LocalNodeManager;
import info.xiancloud.plugin.distribution.loadbalance.UnitRouter;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.sender.AbstractAsyncSender;
import info.xiancloud.plugin.support.mq.mqtt.handle.NotifyHandler;
import info.xiancloud.plugin.util.EnvUtil;

import java.lang.reflect.InvocationTargetException;

/**
 * 远程发送器，负载均衡发送器
 *
 * @author happyyangyuan
 */
public class RemoteSender extends AbstractAsyncSender {

    public RemoteSender(UnitRequest request, NotifyHandler callback) {
        super(request, callback);
    }

    protected void asyncSend() throws Throwable {
        if (EnvUtil.isRemoteSenderDisabled()) {
            callback.callback(UnitResponse.error(Group.CODE_REMOTE_SENDER_DISABLED, EnvUtil.getApplication(),
                    String.format("application '%s' 禁止远程消息发送!", EnvUtil.getApplication())));
        } else {
            sendToRemote(UnitRouter.singleton.loadBalancedInstance(Unit.fullName(unitRequest.getContext().getGroup(),
                    unitRequest.getContext().getUnit())).getNodeId());
        }
    }

    /**
     * send message to remote asynchronously.
     *
     * @param nodeId the destination node id.
     */
    private void sendToRemote(String nodeId) throws InvocationTargetException, IllegalAccessException {
        unitRequest.getContext().setDestinationNodeId(nodeId);
        unitRequest.getContext().setSourceNodeId(LocalNodeManager.LOCAL_NODE_ID);
        LocalNodeManager.sendLoadBalanced(unitRequest, callback);
    }

}
