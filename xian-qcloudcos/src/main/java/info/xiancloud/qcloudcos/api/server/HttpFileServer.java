package info.xiancloud.qcloudcos.api.server;

import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.LOG;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * HTTP 文件服务器
 *
 * @author yyq, happyyangyuan 增加stop动作
 */
public class HttpFileServer {

    private final Object HTTP_SERVER_START_STOP_LOCK = new Object();
    private volatile Channel channel;

    public static void main(String[] args) throws InterruptedException {
        new HttpFileServer().start(8080);
    }

    public HttpFileServer start(int port) {
        synchronized (HTTP_SERVER_START_STOP_LOCK) {
            if (channel == null) {
                try {
                    doStart(port);
                } catch (InterruptedException e) {
                    LOG.error(e);
                }
            } else {
                LOG.warn("已启动，不允许重复启动");
            }
        }
        return this;
    }

    private void doStart(int port) throws InterruptedException {

        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workerGrop = new NioEventLoopGroup();


        ServerBootstrap b = new ServerBootstrap();
        b.group(boosGroup, workerGrop).channel(NioServerSocketChannel.class)
                // .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {

                        // 请求消息解码器
                        channel.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                        channel.pipeline().addLast("http-aggregator", new HttpObjectAggregator(1000000));
                        // 响应消息解码器
                        channel.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                        channel.pipeline().addLast("fileServerHandler", new HttpFileHandler());
                    }
                });

        ChannelFuture f = b.bind(port).sync();
        channel = f.channel();
        LOG.info("qcloud-xml-api 服务器启动成功,路径是：http://" + EnvUtil.getLocalIp() + ":" + port);

        ThreadPoolManager.execute(() -> {
            try {
                // 等待channel关闭通知/回调：客户端或者服务端主动关闭
                channel.closeFuture().sync();
                LOG.info("qcloud-xml-api 服务器完蛋了--");
            } catch (InterruptedException e) {
                LOG.error(e);
            } finally {
                boosGroup.shutdownGracefully();
                workerGrop.shutdownGracefully();
            }
        });
    }

    public void stop() {
        synchronized (HTTP_SERVER_START_STOP_LOCK) {
            if (channel != null) {
                channel.close();
            } else {
                LOG.warn("http netty server根本就未启动过，不需要shutdown!");
            }
        }
    }
}
