package info.xiancloud.rpc.netty.client;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.distribution.MessageType;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.stream.Stream;
import info.xiancloud.plugin.stream.StreamFragmentBean;
import info.xiancloud.plugin.stream.StreamManager;
import info.xiancloud.plugin.distribution.LocalNodeManager;
import info.xiancloud.plugin.support.mq.mqtt.handle.NotifyHandler;
import info.xiancloud.plugin.thread_pool.ThreadPoolManager;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.thread.MsgIdHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.RejectedExecutionException;

/**
 * 改为rpc单向链接后，理论上这个handler不会再被触发了
 */
public class StreamRpcClientHandler extends SimpleChannelInboundHandler<JSONObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JSONObject msg) throws Exception {
        LOG.warn("已关闭此功能");
        if (MessageType.isStream(msg)) {
            try {
                StreamFragmentBean streamFragmentBean = msg.toJavaObject(StreamFragmentBean.class);
                MsgIdHolder.set(streamFragmentBean.getHeader().getMsgId());
                String ssid = streamFragmentBean.getHeader().getId();
                NotifyHandler handler = LocalNodeManager.handleMap.getIfPresent(ssid);
                LocalNodeManager.handleMap.invalidate(ssid);
                //以下写出不会阻塞
                Stream stream = StreamManager.singleton.add(streamFragmentBean);
                if (streamFragmentBean.getHeader().isFirst()) {
                    UnitResponse responseUnitResponse = UnitResponse.success(stream);
                    try {
                        ThreadPoolManager.execute(() -> handler.callback(responseUnitResponse));
                    } catch (RejectedExecutionException threadPoolAlreadyShutdown) {
                        LOG.info("线程池已关闭，这里使用临时线程执行任务，针对停服务时线程池已关闭的情况。");
                        new Thread(() -> handler.callback(responseUnitResponse)).start();
                    }
                }
            } catch (Throwable e) {
                LOG.error(e);
            } finally {
                MsgIdHolder.clear();
            }
        } else
            ctx.fireChannelRead(msg);
    }

}
