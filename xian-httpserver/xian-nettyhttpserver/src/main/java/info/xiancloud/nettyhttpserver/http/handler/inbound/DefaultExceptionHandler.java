package info.xiancloud.nettyhttpserver.http.handler.inbound;

import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.core.util.thread.MsgIdHolder;
import info.xiancloud.nettyhttpserver.http.Config;
import info.xiancloud.nettyhttpserver.http.bean.BadRequestException;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * 遇到异常时的兜底操作
 *
 * @author happyyangyuan
 */
public class DefaultExceptionHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("Exception caught", cause);

        HttpResponseStatus status = (cause instanceof BadRequestException) ? BAD_REQUEST : INTERNAL_SERVER_ERROR;

        String content = StringUtil.getExceptionStacktrace(cause);

        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));

        response.headers().set(CONTENT_TYPE, Config.getContentType());
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

        /*ctx.writeAndFlush(response); todo 爆了异常之后，这里根本写不进去，直接查日志吧！*/
        ctx.close();
        LOG.warn("TODO: 16/9/10 目前是出现了异常则直接关闭,这样对长连接稳定性好像不太有利");
        MsgIdHolder.clear();
    }
}
