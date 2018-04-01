package info.xiancloud.rpcnettyserver;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.distribution.MessageType;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.stream.Stream;
import info.xiancloud.core.stream.StreamFragmentBean;
import info.xiancloud.core.stream.StreamManager;
import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.thread.MsgIdHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * server端处理流请求和响应的handler
 *
 * @author happyyangyuan
 */
public class RpcServerStreamHandler extends SimpleChannelInboundHandler<JSONObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JSONObject msg) {
        if (MessageType.isRequestStream(msg)) {
            LOG.error("RequestStream: Not supported yet!");
        } else if (MessageType.isResponseStream(msg)) {
            try {
                StreamFragmentBean streamFragmentBean = msg.toJavaObject(StreamFragmentBean.class);
                MsgIdHolder.set(streamFragmentBean.getHeader().getMsgId());
                String ssid = streamFragmentBean.getHeader().getId();
                NotifyHandler handler = LocalNodeManager.handleMap.getIfPresent(ssid);
                LocalNodeManager.handleMap.invalidate(ssid);
                //以下写出不会阻塞
                Stream stream = StreamManager.singleton.add(streamFragmentBean);
                if (streamFragmentBean.getHeader().isFirst()) {
                    UnitResponse unitResponse = UnitResponse.createSuccess(stream);
                    ThreadPoolManager.execute(() -> handler.callback(unitResponse));
                }
            } catch (Throwable e) {
                LOG.error(e);
            } finally {
                MsgIdHolder.clear();
            }
            // the pipeline chain ends here.
        } else {
            ctx.fireChannelRead(msg);
            //the pipeline chain continues.
        }
    }

}
