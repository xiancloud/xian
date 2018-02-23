package info.xiancloud.plugin.rpc;

import info.xiancloud.plugin.init.Destroyable;
import info.xiancloud.plugin.init.Initable;
import info.xiancloud.plugin.util.Reflection;

import java.util.List;

/**
 * @author happyyangyuan
 * 单例的rpcServer，对于单机程序可以不用部署本插件的
 */
public interface RpcServer extends Initable, Destroyable {
    List<RpcServer> rpcServers = Reflection.getSubClassInstances(RpcServer.class);
    RpcServer singleton = rpcServers.isEmpty() ? null : rpcServers.get(0);
}
