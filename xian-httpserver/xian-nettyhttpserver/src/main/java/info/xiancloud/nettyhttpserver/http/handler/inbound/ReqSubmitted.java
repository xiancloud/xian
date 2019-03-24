package info.xiancloud.nettyhttpserver.http.handler.inbound;

import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.thread.MsgIdHolder;
import info.xiancloud.nettyhttpserver.http.bean.Request;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author happyyangyuan
 */
public class ReqSubmitted extends SimpleChannelInboundHandler<Request> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request msg) throws Exception {
        LOG.info(">>>>>>>>>>>> 请求已提交给业务层,释放httpRequest的buffer引用并清空$msgId");
        MsgIdHolder.clear();
    }
}
