package info.xiancloud.rpc.netty.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcClientDecoder extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        //为了通信协议的灵活性，我们在这里并没有解码为强类型的bean，而是解码为弱类型json
        JSONObject jsonObject = JSON.parseObject(msg);
        ctx.fireChannelRead(jsonObject);
    }
}
