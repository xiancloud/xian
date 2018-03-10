package info.xiancloud.plugin.netty.http.handler.inbound;

import info.xiancloud.plugin.netty.http.bean.BadRequestException;
import info.xiancloud.plugin.netty.http.bean.ReqQueue;
import info.xiancloud.plugin.netty.http.bean.Request;
import info.xiancloud.plugin.netty.http.handler.outbound.ClearingHandler;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.thread.MsgIdHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;

import java.util.List;

/**
 * 1、解析请求header,得到是否是执行长连接。
 * 我们不采用keep-alive来识别长连接协议,是因为http1.1的keep-alvie值默认为true,但是我们服务端要求默认是短连接。
 * 因此我们自定义了一个名为"LONG_CONNECTION"的header来决定是否开启长连接.
 * 2、将请求缓存到当前连接的上下文内
 *
 * @author happyyangyuan
 */
public class RequestDecoderAux extends MessageToMessageDecoder<FullHttpRequest> {

    @Override
    protected void decode(ChannelHandlerContext ctx, FullHttpRequest msg, List<Object> out) throws Exception {
        LOG.debug("    httpRequest  ---->   UnitRequest Pojo");
        /*if (!HttpMethod.POST.equals(msg.method())) {
            throw new BadRequestException(new IllegalArgumentException("拒绝非POST请求！"));
        }*/
        DecoderResult result = msg.decoderResult();
        if (!result.isSuccess()) {
            throw new BadRequestException(result.cause());
        }
        updateLongConnectionStatus(msg, ctx);
        Request request = new Request(msg, MsgIdHolder.get());
        offerReqQueue(ctx, request);
        out.add(request);
    }

    private void updateLongConnectionStatus(HttpRequest httpRequest, ChannelHandlerContext ctx) {
        String boolStr = httpRequest.headers().get(ClearingHandler.ATTR_NAME_LONG_CONNECTION);
        ctx.channel().attr(ClearingHandler.LONG_CONNECTION).set("true".equalsIgnoreCase(boolStr));
    }

    private void offerReqQueue(ChannelHandlerContext ctx, Request request) {
        if (ctx.channel().attr(ReqQueue.REQ_QUEUE).get() == null) {
            ctx.channel().attr(ReqQueue.REQ_QUEUE).set(new ReqQueue());
        }
        ctx.channel().attr(ReqQueue.REQ_QUEUE).get().offer(request);
    }
}
