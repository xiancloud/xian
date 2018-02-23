package info.xiancloud.rpc.netty.server;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.distribution.LocalNodeManager;
import info.xiancloud.plugin.distribution.MessageType;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.stream.Stream;
import info.xiancloud.plugin.stream.StreamFragmentBean;
import info.xiancloud.plugin.stream.StreamManager;
import info.xiancloud.plugin.support.mq.mqtt.handle.NotifyHandler;
import info.xiancloud.plugin.thread_pool.ThreadPoolManager;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.thread.MsgIdHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * server端处理流请求和响应的handler
 *
 * @author happyyangyuan
 */
public class RpcServerStreamHandler extends SimpleChannelInboundHandler<JSONObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JSONObject msg) throws Exception {
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
                    UnitResponse unitResponse = UnitResponse.success(stream);
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
