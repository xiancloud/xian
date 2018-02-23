package info.xiancloud.rpc.netty.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.StringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author happyyangyuan
 * 我们rpc传输的数据都是json格式的，这里定义个通用的json解析器，避免二次解析消息带来的性能损耗
 */
public class RpcServerJsonDecoder extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        if (StringUtil.isEmpty(msg)) {
            LOG.error("Empty message is not allowed.");
        } else {
            //为了通信协议的灵活性，我们在这里并没有解码为强类型的bean，而是解码为弱类型json
            JSONObject jsonObject = JSON.parseObject(msg);
            ctx.fireChannelRead(jsonObject);
        }
    }
}
