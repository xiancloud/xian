package info.xiancloud.rpcnettyserver;


import info.xiancloud.core.distribution.Node;
import info.xiancloud.core.rpc.RpcServer;
import info.xiancloud.core.util.LOG;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * server based on tcp.
 * This implements the standard xian-core's {@linkplain RpcServer} interface.
 * This is the provider of xian-core's rpc.
 *
 * @author happyyangyuan
 */
public class RpcNettyServer implements RpcServer {

    private final boolean SSL = System.getProperty("XIAN_RPC_SSL") != null;
    private Channel parentChannel;

    private void start() throws Exception {
        if (Node.RPC_PORT < 0) {
            LOG.error("No rpc port is specified, rpc server starting failed.");
            return;
        }
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .option(ChannelOption.WRITE_BUFFER_WATER_MARK,
                        new WriteBufferWaterMark(
                                10 * 1024 * 1024, //10m
                                20 * 1024 * 1024 //20m
                        )
                )
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new RpcServerInitializer(sslCtx));
        parentChannel = b.bind(Node.RPC_PORT).sync().channel();
        parentChannel.closeFuture().addListener(future -> {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            LOG.info("The EventLoopGroup has been terminated completely and all Channels that belong to the group have been closed.");
        });
    }

    @Override
    public void destroy() {
        if (parentChannel != null) {
            parentChannel.close();
        } else {
            LOG.warn("rpc netty server was never started, no need to shutdown.");
        }
    }

    @Override
    public void init() {
        try {
            start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}