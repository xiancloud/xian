package info.xiancloud.rpcnettyclient;

import info.xiancloud.core.Constant;
import info.xiancloud.core.distribution.exception.ApplicationOfflineException;
import info.xiancloud.core.distribution.loadbalance.ApplicationRouter;
import info.xiancloud.core.distribution.service_discovery.ApplicationInstance;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 *
 * @author happyyangyuan
 */
public class RpcNettyClientInitializer extends ChannelInitializer<SocketChannel> {

    private static final StringDecoder DECODER = new StringDecoder();
    private static final StringEncoder ENCODER = new StringEncoder();

    private final SslContext sslCtx;
    private final String HOST;
    private final int PORT;
    /*private final RpcClientHandler CLIENT_HANDLER;*/
    private final String nodeId;

    RpcNettyClientInitializer(SslContext sslCtx, String nodeId) {
        this.sslCtx = sslCtx;
        ApplicationInstance node;
        try {
            node = ApplicationRouter.singleton.getInstance(nodeId);
        } catch (ApplicationOfflineException e) {
            throw new RuntimeException(e);
        }
        HOST = node.getAddress();
        PORT = node.getPort();
        this.nodeId = nodeId;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc(), HOST, PORT));
        }
        pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, new ByteBuf[]{
                Unpooled.wrappedBuffer(Constant.RPC_DELIMITER.getBytes())
        }));
        pipeline.addLast(DECODER);
        pipeline.addLast(ENCODER);
        // and then unit logic.
        pipeline.addLast(new RpcClientHandler(nodeId)/*CLIENT_HANDLER*/);
        pipeline.addLast(new RpcClientDecoder());
        pipeline.addLast(new StreamRpcClientHandler());
        pipeline.addLast(new RpcClientUnitHandler());
    }
}