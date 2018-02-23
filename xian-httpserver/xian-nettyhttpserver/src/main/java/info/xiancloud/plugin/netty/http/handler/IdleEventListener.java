package info.xiancloud.plugin.netty.http.handler;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.netty.http.bean.ReqQueue;
import info.xiancloud.plugin.netty.http.bean.Request;
import info.xiancloud.plugin.netty.http.bean.ResponseWrapper;
import info.xiancloud.plugin.util.LOG;
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
