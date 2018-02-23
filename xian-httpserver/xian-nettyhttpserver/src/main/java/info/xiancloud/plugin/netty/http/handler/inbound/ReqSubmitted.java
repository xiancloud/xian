package info.xiancloud.plugin.netty.http.handler.inbound;

import info.xiancloud.plugin.netty.http.bean.Request;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.thread.MsgIdHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

/**
 * @author happyyangyuan
 */
public class ReqSubmitted extends SimpleChannelInboundHandler<Request> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request msg) throws Exception {
        LOG.debug(">>>>>>>>>>>> 请求已提交给业务层,释放httpRequest的buffer引用并清空$msgId");
        MsgIdHolder.clear();
    }
}
