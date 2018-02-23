package info.xiancloud.plugin.netty.http.handler.outbound;

import info.xiancloud.plugin.http_server.unit.HttpSessionLocalCache;
import info.xiancloud.plugin.netty.http.bean.ReqQueue;
import info.xiancloud.plugin.netty.http.bean.Request;
import info.xiancloud.plugin.netty.http.bean.ResponseWrapper;
import info.xiancloud.plugin.server.IServerResponder;
import info.xiancloud.plugin.server.ServerResponseBean;
import info.xiancloud.plugin.util.LOG;

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
                    new ResponseWrapper(request, responseBean.getResponseBody())
            );
        }
    }
}
