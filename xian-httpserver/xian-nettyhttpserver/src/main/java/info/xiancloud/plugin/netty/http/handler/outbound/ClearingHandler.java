package info.xiancloud.plugin.netty.http.handler.outbound;

import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.thread.MsgIdHolder;
import io.netty.channel.*;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * 处理一些收尾工作
 *
 * @author happyyangyuan
 */
public class ClearingHandler extends ChannelOutboundHandlerAdapter {

    public static final String ATTR_NAME_LONG_CONNECTION = "LONG_CONNECTION";
    /**
     * 默认false,即短连接; 注意,它是一个http连接的状态属性,不同连接不共享此值! 类似ChannelLocal的概念
     * 请求header中的LONG_CONNECTION属性决定是否是执行长连接。
     * 我们不采用keep-alive来识别长连接协议,因为http1.1标准规定,默认keep-alvie为true,但是我们服务端要求默认是短连接。
     * 因此,我们自定义了一个header: LONG_CONNECTION来自定义长连接的建立.
     */
    public static final AttributeKey<Boolean> LONG_CONNECTION = AttributeKey.valueOf(ATTR_NAME_LONG_CONNECTION);


    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        promise.addListener(future -> {
            if (!ctx.channel().attr(ClearingHandler.LONG_CONNECTION).get()) {
                LOG.debug("准备关闭短连接");
                ctx.close().addListener(closeFuture -> {
                    LOG.debug("短连接已关闭,资源已释放!!");
                    MsgIdHolder.clear();
                });
            } else {
                MsgIdHolder.clear();
            }
        });
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        try {
            LOG.info("如果之前的handler主动关闭了连接,那么这里清理掉$msgId");
            super.close(ctx, promise);
        } finally {
            MsgIdHolder.clear();
        }
    }
}
