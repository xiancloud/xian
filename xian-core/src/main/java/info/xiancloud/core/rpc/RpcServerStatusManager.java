package info.xiancloud.core.rpc;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import info.xiancloud.core.*;
import info.xiancloud.core.distribution.MessageType;
import info.xiancloud.core.distribution.exception.ApplicationOfflineException;
import info.xiancloud.core.distribution.loadbalance.ApplicationRouter;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.mq.TransferQueueUtil;
import info.xiancloud.core.util.LOG;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 记录服务端状态机
 *
 * @author happyyangyuan
 * @deprecated rpc状态现已经写死，不允许修改
 */
public class RpcServerStatusManager implements Unit {

    @Override
    public Group getGroup() {
        return SystemGroup.singleton;
    }

    private static AtomicBoolean ENABLE_RPC = new AtomicBoolean(true);

    private static final LoadingCache<String, RpcServerStatus> rpcServerStatusMap = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(new CacheLoader<String, RpcServerStatus>() {
                public RpcServerStatus load(String nodeId) throws Exception {
                    return new RpcServerStatus(nodeId);
                }
            });

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("rpc开关").setPublic(false).setBroadcast();
    }

    @Override
    public String getName() {
        return "rpcSwitch";
    }

    @Override
    public Input getInput() {
        return new Input().add("enable", boolean.class, "", REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        boolean enable = msg.get("enable", boolean.class);
        ENABLE_RPC.set(enable);
        handler.handle(UnitResponse.createSuccess());
    }

    private static final class RpcServerStatus {
        private final String nodeId;
        private AtomicLong lastTryTime = new AtomicLong(-1);
        private AtomicBoolean lastSuccessful = new AtomicBoolean(false);

        private RpcServerStatus(String nodeId) {
            this.nodeId = nodeId;
        }

        private boolean canTry() {
            try {
                LOG.debug("//每次都检查注册的服务最新rpc状态，以支持远程服务器动态切换其rpc状态");
                boolean rpcEnabled = ApplicationRouter.singleton.getInstance(nodeId).getPayload().getPort() > 0;
                if (!rpcEnabled) {
                    return false;
                }
                if (lastSuccessful.get()) {
                    LOG.debug("//注意这里逻辑，上次成功，那么认为本次基本能成功，不发ping，避免ping泛滥影响性能");
                    return true;
                }
                if (System.nanoTime() - lastTryTime.get() > 60 * 1000 * 1000000L) {
                    LOG.debug(" /*虽然上次失败，但是若超过一分钟可再次尝试*/");
                    try {
                        RpcClient.singleton.request(nodeId, MessageType.PING_MSG);
                        LOG.debug(" /*rpc ping通*/");
                        lastSuccessful.set(true);
                        return true;
                    } catch (Throwable e) {
                        LOG.debug(" /*rpc ping不通*/");
                        lastSuccessful.set(false);
                    }
                }
                return false;
            } catch (ApplicationOfflineException e) {
                LOG.error(e);
                return false;
            } finally {
                lastTryTime.set(System.nanoTime());
            }
        }
    }

    /**
     * @param nodeId 节点id/队列名
     */
    public static boolean canTry(String nodeId) {
        if (TransferQueueUtil.isTransferQueue(nodeId)) {
            LOG.debug("中转节点直接发mqtt消息的");
            return false;
        }
        if (!ENABLE_RPC.get()) {
            return false;
        }
        return rpcServerStatusMap.getUnchecked(nodeId).canTry();
    }

    public static void updateStatus(String nodeId, boolean available) {
        synchronized (rpcServerStatusMap.getUnchecked(nodeId)) {
            rpcServerStatusMap.getUnchecked(nodeId).lastSuccessful.set(available);
            rpcServerStatusMap.getUnchecked(nodeId).lastTryTime.set(System.nanoTime());
        }
    }

    public static RpcServerStatus getRpcServerStatus(String nodeId) {
        return rpcServerStatusMap.getUnchecked(nodeId);
    }
}
