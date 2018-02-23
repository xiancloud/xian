package info.xiancloud.plugin.netty;

import info.xiancloud.plugin.netty.http.Config;
import info.xiancloud.plugin.netty.http.channel_initializer.DefaultInitializer;
import info.xiancloud.plugin.util.LOG;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {

    static final Object HTTP_SERVER_START_STOP_LOCK = new Object();
    private Channel parentChannel;

    /**
     * 异步
     */
    public void startServer() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            synchronized (HTTP_SERVER_START_STOP_LOCK) {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new DefaultInitializer())
                        .option(ChannelOption.SO_BACKLOG, Config.getBacklog())
                        .option(ChannelOption.SO_REUSEADDR, true);
                LOG.info("[Netty httpServer]  About to bind and start to accept incoming connections on port " + Config.getPort());
                parentChannel = b.bind(Config.getPort()).sync().channel();
                LOG.info("[Netty httpServer]  Started on port " + Config.getPort());
            }
            parentChannel.closeFuture().addListener(future -> {
                LOG.debug("until the server socket is closed.");
                LOG.info("[Netty httpServer] 准备shutdown netty server");
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
                LOG.info("[Netty httpServer] netty server shutdown完毕!");
            });
        } catch (Throwable e) {
            LOG.error("http netty server 启动失败!", e);
        }
    }

    public void stopServer() {
        synchronized (HTTP_SERVER_START_STOP_LOCK) {
            if (parentChannel != null) {
                parentChannel.close();
            } else {
                LOG.warn("http netty server根本就未启动过，不需要shutdown!");
            }
        }
    }

}
