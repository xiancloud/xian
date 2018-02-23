package info.xiancloud.plugin.netty.http.handler.inbound;

import info.xiancloud.plugin.netty.http.trace.OuterMsgId;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.StringUtil;
import info.xiancloud.plugin.util.thread.MsgIdHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

import java.net.InetSocketAddress;

/**
 * 当前管道每次收到一个完整的http请求即执行一些初始化动作,比如初始化一个$msgId
 *
 * @author happyyangyuan
 */
public class ReqReceived extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest httpRequest = (FullHttpRequest) msg;
            String outerMsg = OuterMsgId.get(httpRequest);
            if (StringUtil.isEmpty(outerMsg)) {
                MsgIdHolder.init();
            } else {
                MsgIdHolder.set(outerMsg);
                LOG.info("xian独立节点传入了msgId=" + outerMsg);
            }
            String $ip = httpRequest.headers().get("X-Real-IP");
            if (StringUtil.isEmpty($ip)) {
                InetSocketAddress address = (InetSocketAddress) (ctx.channel().remoteAddress());
                $ip = address.getAddress().getHostAddress();
            }
            LOG.info(String.format("收到来自%s的FullHttpRequest \r\n %s!", $ip, msg));
            ctx.fireChannelRead(msg);
        } else {
            LOG.info("收到chucked http message...直接忽略，等待组装完毕");
        }
    }
}
