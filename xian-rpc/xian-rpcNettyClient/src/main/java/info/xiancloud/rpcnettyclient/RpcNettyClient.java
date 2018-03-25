package info.xiancloud.rpcnettyclient;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Constant;
import info.xiancloud.core.distribution.exception.ApplicationInstanceOfflineException;
import info.xiancloud.core.distribution.loadbalance.ApplicationRouter;
import info.xiancloud.core.distribution.service_discovery.ApplicationInstance;
import info.xiancloud.core.rpc.RpcClient;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.LOG;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The rpc client implemented via the netty framework. Singleton as it is.
 *
 * @author happyyangyuan
 */
public final class RpcNettyClient implements RpcClient {

    private static final boolean SSL = System.getProperty("XIAN_RPC_SSL") != null;
    private static final Map<String, Channel> nodeId_to_connectedChannel_map = new ConcurrentHashMap<>();
    private final static Lock lock = new ReentrantLock();//锁的粒度目前这样足矣

    /**
     * 异步发送器，如果要同步，请在你自己那一层阻塞，如果消息发送失败则返回false
     */
    public boolean request(String nodeId, String message) {
        if (!channelAvailable(nodeId)) {
            try {
                lazyInit(nodeId);
            } catch (ApplicationInstanceOfflineException nodeOffline) {
                LOG.warn("Warning, node is offline.", nodeOffline);
                return false;
            } catch (Exception e) {
                LOG.error("Error, unexpected exception.", e);
                return false;
            }
        }
        if (LOG.isDebugEnabled())
            LOG.debug(String.format(">>>> rpc to %s  >>> remoteAddress=%s", nodeId, nodeId_to_connectedChannel_map.get(nodeId).remoteAddress()));
        nodeId_to_connectedChannel_map.get(nodeId).writeAndFlush(message + Constant.RPC_DELIMITER);
        return true;
    }

    /**
     * 当连接废弃时，需要将缓存map中的那个废弃连接释放掉，此操作是并发安全的
     */
    static void removeClosedChannel(String nodeId) {
        if (lock.tryLock()) {
            try {
                nodeId_to_connectedChannel_map.remove(nodeId);
                LOG.info(String.format("RpcClient:与%s的空闲长连接缓存释放完毕...", nodeId));
            } finally {
                lock.unlock();
            }
        } else {
            LOG.info(String.format("RpcClient删除缓存的与%s的废弃的连接：已经有新连接正在建立，那么这里不做操作，由新建连接的线程去覆盖掉缓存中的废弃连接", nodeId));
        }
    }

    private static boolean channelAvailable(String nodeId) {
        boolean exists = nodeId_to_connectedChannel_map.get(nodeId) != null;
        if (!exists) return false;
        boolean open = nodeId_to_connectedChannel_map.get(nodeId).isOpen(),
                active = nodeId_to_connectedChannel_map.get(nodeId).isActive(),
                writable = nodeId_to_connectedChannel_map.get(nodeId).isWritable(),
                totalAvailable = open && active/* && writable  不再将writable计入到channelAvailable判断依据内 避免流传输时被强行新起了rpc连接而出现问题的情况发生*/;
        if (!totalAvailable) {
            LOG.warn(new Throwable(String.format("RpcClient: " +
                    "现存rpc channel不可用，请检查具体原因。" +
                    "channelStatus exists=%s open=%s active=%s writable=%s", true, open, active, writable)));
        }
        if (!writable)
            LOG.warn(new Throwable(String.format("RpcClient: " +
                    "现存rpc channel 写缓冲区占用空间超过设定的水位值，请检查具体原因。" +
                    "channelStatus exists=%s open=%s active=%s writable=%s", true, open, active, writable)));
        return totalAvailable;
    }

    /**
     * @param nodeId The node's id to which you want to initialize the connection. This method is thread-safe because it is synchronized.
     * @throws info.xiancloud.plugin.distribution.exception.ApplicationInstanceOfflineException Because the destination node is offline, of cause you cannot initialize the connection.
     * @throws Exception                                                                        Other unknown exceptions.
     */
    private static void lazyInit(String nodeId) throws Exception {
        lock.lock();
        String host = null;
        int port = -1;
        try {
            if (channelAvailable(nodeId)) {
                LOG.debug(String.format("RpcClient:已经存在一个与%s的长连接，不再新建连接.", nodeId));
                return;
            }
            LOG.info(String.format("RpcClient:开始新建与%s的长连接...", nodeId));
            ApplicationInstance node = ApplicationRouter.singleton.getInstance(nodeId);
            //如果是在同一台主机内部部署的两个节点，那么避免走交换机、路由器了
            host = Objects.equals(node.getAddress(), EnvUtil.getLocalIp()) ? "127.0.0.1" : node.getAddress();
            port = node.getPort();
            final SslContext sslCtx;
            if (SSL) {
                sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            } else {
                sslCtx = null;
            }

            EventLoopGroup group = new NioEventLoopGroup(1);
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .option(ChannelOption.WRITE_BUFFER_WATER_MARK,
                            new WriteBufferWaterMark(
                                    10 * 1024 * 1024, //10m
                                    20 * 1024 * 1024 //20m
                            )
                    )
                    .channel(NioSocketChannel.class)
                    .handler(new RpcNettyClientInitializer(sslCtx, nodeId))
                    //连接超时时间设置为100ms，fail-fast
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 100);
            Channel connectedChannel = b.connect(host, port).sync().channel();
            connectedChannel.closeFuture().addListener(future -> {
                        group.shutdownGracefully();
                        LOG.info("The EventLoopGroup has been terminated completely and all Channels that belong to the group have been closed.");
                    }
            );
            nodeId_to_connectedChannel_map.put(nodeId, connectedChannel);
            LOG.info(new JSONObject() {{
                put("toNodeId", nodeId);
                put("rpcRemoteAddress", connectedChannel.remoteAddress().toString());
                put("type", "rpcChannelConnected");
                put("description", String.format("RpcClient:与%s的长连接建立完毕, remoteAddress=%s", nodeId, connectedChannel.remoteAddress()));
            }}.toJSONString());
        } catch (Throwable e) {
            throw new Exception(String.format("与远程节点%s建立长连接失败:host=%s,port=%s", nodeId, host, port), e);
        } finally {
            lock.unlock();
        }
    }

    public void destroy() {
        for (Channel channel : nodeId_to_connectedChannel_map.values()) {
            channel.close();
        }
        nodeId_to_connectedChannel_map.clear();
    }
}
