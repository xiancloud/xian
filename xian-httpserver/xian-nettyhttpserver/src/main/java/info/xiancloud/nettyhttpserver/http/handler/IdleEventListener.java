package info.xiancloud.nettyhttpserver.http.handler;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Group;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.nettyhttpserver.http.bean.ReqQueue;
import info.xiancloud.nettyhttpserver.http.bean.Request;
import info.xiancloud.nettyhttpserver.http.bean.ResponseWrapper;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.List;

/**
 * @author happyyangyuan
 */
public class IdleEventListener extends ChannelDuplexHandler {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            LOG.info(ctx.channel().remoteAddress() + "超时类型：" + event.state().name());
            if (event.state() == IdleState.WRITER_IDLE) {
                List<Request> timeoutRequests = ctx.channel().attr(ReqQueue.REQ_QUEUE).get().removeTimeout();
                for (Request timeoutRequest : timeoutRequests) {
                    timeoutRequest.getChannelHandlerContext().writeAndFlush(buildTimeoutResponse(timeoutRequest));
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    private ResponseWrapper buildTimeoutResponse(Request request) {
        LOG.info("我只是想看看IdleEventListener的线程");
        return new ResponseWrapper(request, UnitResponse.create(new JSONObject() {{
            put("code", Group.CODE_TIME_OUT);
            put("data", "服务器处理请求超时!!!");
            put("$msgId", request.getMsgId());
        }}).toJSONString());
    }
}
