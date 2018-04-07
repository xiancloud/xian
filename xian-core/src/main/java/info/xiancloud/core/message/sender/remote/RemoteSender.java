package info.xiancloud.core.message.sender.remote;

import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.distribution.loadbalance.UnitRouter;
import info.xiancloud.core.Group;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.sender.AbstractAsyncSender;
import info.xiancloud.core.util.EnvUtil;

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

    protected void asyncSend() throws Exception {
        if (EnvUtil.isRemoteSenderDisabled()) {
            callback.callback(UnitResponse.createError(Group.CODE_REMOTE_SENDER_DISABLED, EnvUtil.getApplication(),
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
