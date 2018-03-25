package info.xiancloud.nettyhttpserver.http.handler.outbound;

import info.xiancloud.core.util.LOG;
import info.xiancloud.httpserver.core.unit.HttpSessionLocalCache;
import info.xiancloud.nettyhttpserver.http.bean.ReqQueue;
import info.xiancloud.nettyhttpserver.http.bean.Request;
import info.xiancloud.nettyhttpserver.http.bean.ResponseWrapper;
import info.xiancloud.gateway.server.IServerResponder;
import info.xiancloud.gateway.server.ServerResponseBean;

/**
 * httpServer asynchronous response subclass
 *
 * @author happyyangyuan
 */
public class NettyHttpResponseListener implements IServerResponder {

    @Override
    public void response(ServerResponseBean responseBean) {
        Request request = (Request) HttpSessionLocalCache.removeSession(responseBean.getMsgId());
        if (request == null) {
            LOG.error("httpServer内找不到msgId " + responseBean.getMsgId() + " 的请求记录", new RuntimeException());
        } else {
            request.getChannelHandlerContext().channel().attr(ReqQueue.REQ_QUEUE).get().writeAndFlush(
                    new ResponseWrapper(request, responseBean.getResponseBody()).setHttpContentType(responseBean.getHttpContentType())
            );
        }
    }
}
