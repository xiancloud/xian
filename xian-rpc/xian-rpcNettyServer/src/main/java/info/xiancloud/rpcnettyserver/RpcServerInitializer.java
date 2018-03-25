package info.xiancloud.rpcnettyserver;


import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Constant;
import info.xiancloud.core.util.LOG;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author happyyangyuan
 */
public class RpcServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final StringDecoder DECODER = new StringDecoder();
    private static final StringEncoder ENCODER = new StringEncoder();
    /**
     * @deprecated handler是有状态的不应该共享多个连接
     */
    private static /*final*/ RpcServerDefaultHandler SERVER_HANDLER /*= new RpcServerHandler()*/;
    /**
     * @deprecated handler是有状态的不应该共享多个连接
     */
    private static /*final*/ RpcServerIdleStateHandler IDLE_EVENT_HANDLER/* = new RpcServerIdleEventHandler()*/;

    private final SslContext sslCtx;

    RpcServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }

        // Add the text line codec combination first,分隔符就是"\r\n$end!"
        pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE,
                new ByteBuf[]{
                        Unpooled.wrappedBuffer(Constant.RPC_DELIMITER.getBytes())
                }
        ));
        // the encoder and decoder are static as these are sharable
        pipeline.addLast(DECODER);
        pipeline.addLast(ENCODER);

        pipeline.addLast(/*IDLE_EVENT_HANDLER*/new RpcServerIdleStateHandler());
        pipeline.addLast(new ChannelDuplexHandler() {
            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                if (evt instanceof IdleStateEvent) {
                    IdleStateEvent e = (IdleStateEvent) evt;
                    if (e.state() == IdleState.ALL_IDLE) {
                        LOG.info(new JSONObject() {{
                            put("type", "rpcIdle");
                            put("description", String.format("关闭闲置连接: timeOut=%sms", RpcServerIdleStateHandler.IDLE_TIMEOUT_IN_MILLI));
                            put("detail", ctx.channel().remoteAddress());
                        }});
                        ctx.close();
                    } else {
                        LOG.info("Nothing need to do: " + e.state().name());
                    }
                }
            }
        });
        // 注意：readHandler必须在idleHandler监听器之后！readHandler必须在idleHandler监听器之后！readHandler必须在idleHandler监听器之后！
        // 接上：否则，就没法触发idleHandler了
        pipeline.addLast(new RpcServerJsonDecoder());
        pipeline.addLast(new RpcServerStreamHandler());
        pipeline.addLast(new RpcServerDefaultHandler());
    }
}