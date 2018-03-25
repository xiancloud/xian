package info.xiancloud.rpcnettyclient;

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
