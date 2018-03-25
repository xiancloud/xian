package info.xiancloud.rpcnettyclient;


import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.util.LOG;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Handles a client-side channel.
 *
 * @author happyyangyuan
 */
public class RpcClientHandler extends ChannelInboundHandlerAdapter {

    private String nodeId;

    RpcClientHandler(String nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.error(cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOG.info(new JSONObject() {{
            put("description", "rpcClient 申请释放空闲的长连接: remoteAddress=" + ctx.channel().remoteAddress() + " nodeId=" + nodeId);
            put("toNodeId", nodeId);
            put("rpcRemoteAddress", ctx.channel().remoteAddress().toString());
            put("type", "rpcChannelInactive");
        }}.toJSONString());
        RpcNettyClient.removeClosedChannel(nodeId);
        ctx.channel().close();
    }
}
