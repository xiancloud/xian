package info.xiancloud.core.message.sender.xhash;

import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.distribution.exception.UnitOfflineException;
import info.xiancloud.core.distribution.exception.UnitUndefinedException;
import info.xiancloud.core.distribution.loadbalance.UnitRouter;
import info.xiancloud.core.distribution.service_discovery.UnitInstance;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.sender.AbstractAsyncSender;
import info.xiancloud.core.message.sender.local.RoutedLocalAsyncSender;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.consistent_hash.Shard;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * xhash属性消息定点发送器
 *
 * @author happyyangyuan
 */
public class XhashSender extends AbstractAsyncSender {

    public XhashSender(UnitRequest request, NotifyHandler handler) {
        super(request, handler);
    }

    @Override
    protected void asyncSend() {
        String group = unitRequest.getContext().getGroup(),
                unit = unitRequest.getContext().getUnit();
        try {
            List<UnitInstance> unitInstances = UnitRouter.SINGLETON.allInstances(Unit.fullName(group, unit));
            List<String> clientIds = new ArrayList<>();
            for (UnitInstance clientInfo : unitInstances) {
                clientIds.add(clientInfo.getNodeId());
            }
            String[] xhashNames = unitInstances.get(0).getPayload().getInput().getXhashNames();
            Collections.sort(clientIds);//sorting is for constant-hash requirement.
            String clientId = new Shard<>(clientIds).getShardInfo(xhashString(xhashNames));
            if (clientId.equals(LocalNodeManager.LOCAL_NODE_ID)) {
                new RoutedLocalAsyncSender(unitRequest, callback).send();
            } else {
                unitRequest.getContext().setDestinationNodeId(clientId);
                LocalNodeManager.send(unitRequest, callback);
            }
        } catch (UnitOfflineException | UnitUndefinedException e) {
            LOG.error("代码写错了吧？ 进入xhashSender的前提就是unit在线！", e);
            callback.callback(UnitResponse.createException(e));
        }
    }

    private String xhashString(String[] xKeys) {
        /*Integer hashCode = 0;
        for (int i = 0; i < xKeys.length; i++) {
            hashCode += map.get(xKeys[i]).hashCode() * (i + 1);
        }
        return hashCode.toString();*/
        StringBuilder hashStringBuilder = new StringBuilder();
        for (String xKey : xKeys) {
            hashStringBuilder.append(unitRequest.get(xKey).toString()).append(File.separatorChar);
        }
        String hashStr = hashStringBuilder.substring(0, hashStringBuilder.length() - 1);
        LOG.debug("本次xhash的key= " + hashStr);
        return hashStr;
    }
}
