package info.xiancloud.nettyhttpserver.http.handler.outbound;

import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.nettyhttpserver.http.Config;
import info.xiancloud.nettyhttpserver.http.bean.ResponseWrapper;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


/**
 * 构造FullHttpResponse对象!
 *
 * @author happyyangyuan
 */
public class HttpResponseBuilder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object responseMsg, ChannelPromise promise) throws Exception {
        ResponseWrapper responseWrapper = (ResponseWrapper) responseMsg;
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(responseWrapper.getResPayload().toString(), Config.defaultUtf8()));
        httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType(responseWrapper));
        httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
        LOG.debug("我们不适用http1.1协议默认的keep-alive = true以及请求的header中的keep-alive来决定连接是否keep-alvie而是使用自定义的header: LONG_CONNECTION");
        Boolean keepAlive = ctx.channel().attr(ClearingHandler.LONG_CONNECTION).get();
        if (keepAlive) {
            httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        } else {
            httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        }
        /*LOG.info("<<<<<<<<<<<< netty 返回 HttpResponse: \r\n" + httpResponse);*/
        super.write(ctx, httpResponse, promise);
    }

    /**
     * we get content type from unit response if specified, or defaults to {@link Config#getContentType() application/json}
     */
    private String contentType(ResponseWrapper responseWrapper) {
        return StringUtil.isEmpty(responseWrapper.getHttpContentType()) ? Config.getContentType() : responseWrapper.getHttpContentType();
    }

}
