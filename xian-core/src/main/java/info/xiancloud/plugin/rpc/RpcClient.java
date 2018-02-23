package info.xiancloud.plugin.rpc;

import info.xiancloud.plugin.init.Destroyable;
import info.xiancloud.plugin.util.Reflection;

import java.util.List;

/**
 * @author happyyangyuan
 * 单例的rpcClient
 */
public interface RpcClient extends Destroyable {

    List<RpcClient> rpcClients = Reflection.getSubClassInstances(RpcClient.class);

    RpcClient singleton = rpcClients.isEmpty() ? null : rpcClients.get(0);

    /**
     * 异步发送器，如果要同步，请在你自己那一层阻塞，如果消息发送失败则抛出RuntimeException
     */
    boolean request(String nodeId, String message);
}
