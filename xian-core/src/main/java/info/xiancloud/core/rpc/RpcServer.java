package info.xiancloud.core.rpc;

import info.xiancloud.core.init.Destroyable;
import info.xiancloud.core.init.Initable;
import info.xiancloud.core.util.Reflection;
import info.xiancloud.core.init.Destroyable;
import info.xiancloud.core.init.Initable;
import info.xiancloud.core.util.Reflection;

import java.util.List;

/**
 * @author happyyangyuan
 * 单例的rpcServer，对于单机程序可以不用部署本插件的
 */
public interface RpcServer extends Initable, Destroyable {
    List<RpcServer> rpcServers = Reflection.getSubClassInstances(RpcServer.class);
    RpcServer singleton = rpcServers.isEmpty() ? null : rpcServers.get(0);
}
