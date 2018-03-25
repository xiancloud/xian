package info.xiancloud.rpcnettyclient;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.thread.MsgIdHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 改为单向连接后，理论上这个handler不会再触发了
 *
 * @author happyyangyuan
 */
public class RpcClientUnitHandler extends SimpleChannelInboundHandler<JSONObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JSONObject response) throws Exception {
        try {
            MsgIdHolder.set(response.getString("$msgId"));
            logRpcFly(response, ctx);
            String ssid = response.remove("$ssid").toString();
            NotifyHandler handler = LocalNodeManager.handleMap.getIfPresent(ssid);
            LocalNodeManager.handleMap.invalidate(ssid);
            if (handler == null) {
                LOG.error(String.format("ssid=%s的消息没有找到对应的notifyHandler!整个消息内容=%s,", ssid, response), new Throwable());
                return;
            }
            UnitResponse responseUnitResponse = UnitResponse.create(response);
            ThreadPoolManager.execute(() -> handler.callback(responseUnitResponse));
            /*try {
                不再需要单独检测线程池被销毁而使用独立线程了，threadPoolManager已支持
            } catch (RejectedExecutionException threadPoolAlreadyShutdown) {
                LOG.info("线程池已关闭，这里使用临时线程执行任务，针对停服务时线程池已关闭的情况。");
                new Thread(() -> handler.callback(responseUnitResponse)).start();
            }*/
        } catch (Throwable e) {
            LOG.error(e);
        } finally {
            MsgIdHolder.clear();
        }
    }

    /**
     * 打印rpc消息传输耗时等信息:术语rpcFly
     */
    private static void logRpcFly(JSONObject msgJson, ChannelHandlerContext ctx) {
        try {
            String ssid = msgJson.getString("$ssid");
            String from = msgJson.getString("$LOCAL_NODE_ID");
            String msgType = msgJson.getString("$msgType");
            if (ssid != null) {
                long start = (long) msgJson.remove("$timestampMs");//目前是用完删掉,避免传递到业务层
                LOG.debug(new JSONObject() {{
                    put("cost", System.currentTimeMillis() - start);
                    put("type", "rpcFly");
                    put("ssid", ssid);
                    put("from", from);
                    /*put("length", msgStr.length());*/
                    /*put("fullMsg", msgStr);*/
                    put("msgType", msgType);
                    put("client", ctx.channel().localAddress());
                    put("server", ctx.channel().remoteAddress());
                }});
            }
        } catch (Throwable e) {
            LOG.error(e);
        }
    }
}
