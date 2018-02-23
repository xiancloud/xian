package info.xiancloud.plugin.netty.http.channel_initializer;

import info.xiancloud.plugin.netty.http.Config;
import info.xiancloud.plugin.netty.http.bean.ReqQueue;
import info.xiancloud.plugin.netty.http.handler.IdleEventListener;
import info.xiancloud.plugin.netty.http.handler.inbound.*;
import info.xiancloud.plugin.netty.http.handler.outbound.ClearingHandler;
import info.xiancloud.plugin.netty.http.handler.outbound.HttpResponseBuilder;
import info.xiancloud.plugin.netty.http.handler.outbound.ResReceived;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author happyyangyuan
 */
public class DefaultInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        final ChannelPipeline p = ch.pipeline();
        initOutboundHandlers(p);
        initInboundHandlers(p);
        initIdleStateHandlers(p);
    }

    private void initOutboundHandlers(ChannelPipeline p) {
        p.addLast("httpResponseEncoder", new HttpResponseEncoder());
        p.addLast("clearingRespHandler", new ClearingHandler());
        p.addLast("responseHeader", new HttpResponseBuilder());
        p.addLast("resReceived", new ResReceived());
    }

    private void initInboundHandlers(ChannelPipeline p) {
        p.addLast("httpRequestDecoder", new HttpRequestDecoder());
        p.addLast("httpAggregator", new HttpObjectAggregator(Config.getClientMaxBodySize()));
        p.addLast("reqReceived", new ReqReceived());
        p.addLast("reqDecoderAux", new RequestDecoderAux());
        p.addLast("businessHandler", new BusinessHandler());
        p.addLast("reqSubmitted", new ReqSubmitted());
        p.addLast("exceptionHandler", new DefaultExceptionHandler());
    }

    private void initIdleStateHandlers(ChannelPipeline p) {
        p.addLast("idleStateHandler", new IdleStateHandler(ReqQueue.TIMEOUT_IN_MILLIS, ReqQueue.TIMEOUT_IN_MILLIS, ReqQueue.TIMEOUT_IN_MILLIS, TimeUnit.MILLISECONDS));
        p.addLast("idleEventListener", new IdleEventListener());
    }

}

