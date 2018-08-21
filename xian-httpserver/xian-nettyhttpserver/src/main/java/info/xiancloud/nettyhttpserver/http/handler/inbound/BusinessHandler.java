package info.xiancloud.nettyhttpserver.http.handler.inbound;

import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.httpserver.core.unit.HttpSessionLocalCache;
import info.xiancloud.nettyhttpserver.http.bean.Request;
import info.xiancloud.gateway.scheduler.IAsyncForwarder;
import info.xiancloud.gateway.server.ServerRequestBean;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;

/**
 * business handler
 *
 * @author happyyangyuan
 */
public class BusinessHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object request0) {
        Request request = (Request) request0;
        request.setChannelHandlerContext(ctx);
        forUseCase(ctx, request);
        ctx.fireChannelRead(request);
    }

    private void forUseCase(ChannelHandlerContext ctx, Request request) {
        HttpSessionLocalCache.cacheSession(request.getMsgId(), request);
        //from nginx or some other lb (tencentCloud/aliyun/aws)
        String ip = request.getHttpRequest().headers().get("X-Real-IP");
        if (StringUtil.isEmpty(ip)) {
            InetSocketAddress address = (InetSocketAddress) (ctx.channel().remoteAddress());
            ip = address.getAddress().getHostAddress();
        }
        ServerRequestBean requestBean = new ServerRequestBean();
        requestBean.setUri(request.getUrl());
        requestBean.setBody(request.getBody());
        requestBean.setMsgId(request.getMsgId());
        requestBean.setIp(ip);
        requestBean.setHeader(request.getHeader());
        IAsyncForwarder scheduler = IAsyncForwarder.getForwarder(requestBean.getUri());
        scheduler.forward(requestBean);
        LOG.info("UnitRequest is committed.");
        LOG.debug("说明:以上IApiGatewayRequest.submitRequest是异步提交任务,即内部启用了线程池来加载任务了,因此这里不需提交线程池来执行任务");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        LOG.info("channelInactive !");
    }

}
