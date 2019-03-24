package info.xiancloud.nettyhttpserver.http.handler.inbound;

import info.xiancloud.core.Group;
import info.xiancloud.core.message.HttpContentType;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.gateway.controller.URIBean;
import info.xiancloud.gateway.scheduler.IAsyncForwarder;
import info.xiancloud.gateway.server.IServerResponder;
import info.xiancloud.gateway.server.ServerRequestBean;
import info.xiancloud.gateway.server.ServerResponseBean;
import info.xiancloud.httpserver.core.unit.HttpSessionLocalCache;
import info.xiancloud.nettyhttpserver.http.bean.Request;
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
        HttpSessionLocalCache.cacheSession(request.getMsgId(), request);
        if (checkAndRespond(request)) {
            forUseCase(ctx, request);
            ctx.fireChannelRead(request);
        }
    }

    /**
     * Check the request and respond a bad request response if checking is not passed.
     *
     * @param request the request to be checked.
     * @return true if checking passed
     */
    private boolean checkAndRespond(Request request) {
        if (!URIBean.checkUri(request.getUrl())) {
            ServerResponseBean responseBean = new ServerResponseBean();
            responseBean.setMsgId(request.getMsgId());
            responseBean.setHttpContentType(HttpContentType.APPLICATION_JSON);
            responseBean.setResponseBody(UnitResponse.createError(Group.CODE_BAD_REQUEST, null, String.format("URI %s is illegal.", request.getUrl()))
                    .toVoJSONString());
            IServerResponder.singleton.response(responseBean);
            return false;
        }
        return true;
    }

    private void forUseCase(ChannelHandlerContext ctx, Request request) {
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
