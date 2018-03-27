package info.xiancloud.rpcnettyserver;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Constant;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.distribution.MessageType;
import info.xiancloud.core.message.IdManager;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.sender.local.DefaultLocalAsyncSender;
import info.xiancloud.core.sequence.ISequencer;
import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.thread.MsgIdHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.function.Consumer;

/**
 * Handles a server-side channel.
 * <p>
 * not @Sharable: stateful handler can not be sharable!
 * <p>
 * see https://my.oschina.net/xinxingegeya/blog/295577
 *
 * @author happyyangyuan
 */
public class RpcServerDefaultHandler extends SimpleChannelInboundHandler<JSONObject> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        LOG.info(new JSONObject() {{
            put("myMsg", "rpc connected.");
            put("type", "serverSideChannelActive");
            put("server", ctx.channel().localAddress());
            put("client", ctx.channel().remoteAddress());
        }});
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, JSONObject untRequestOrResponse) {
        if (MessageType.isPing(untRequestOrResponse)) {
            LOG.info("Received a ping from client size.");
        } else {
            processMsg(ctx, untRequestOrResponse);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.error(cause);
        ctx.close();
    }

    private void processMsg(final ChannelHandlerContext ctx, final JSONObject json) {
        try {
            IdManager.makeSureMsgId(json);
            if (MessageType.isDefaultRequest(json)) {
                UnitRequest request = json.toJavaObject(UnitRequest.class);
                request.getContext().setFromRemote(true);
                String group = request.getContext().getGroup(),
                        unit = request.getContext().getUnit();
                final Consumer<String> backPayloadConsumerOnFailure = payload -> {
                    LOG.info("rpc server --> client发送失败了，因此这里复用当前长连接响应消息");
                    ctx.writeAndFlush(payload + Constant.RPC_DELIMITER);
                };
                ISequencer.build(group, unit, json).sequence(
                        new DefaultLocalAsyncSender(request, new NotifyHandler() {
                            protected void handle(UnitResponse unitResponse) {
                                LocalNodeManager.sendBack(unitResponse, backPayloadConsumerOnFailure);
                            }
                        }),
                        new NotifyHandler() {
                            protected void handle(UnitResponse failureOut) {
                                LocalNodeManager.sendBack(failureOut, backPayloadConsumerOnFailure);
                            }
                        });
            } else if (MessageType.isDefaultResponse(json)) {
                LOG.debug("这是非常重要的说明：" +
                        "1、客户端发给外部节点的请求期待的响应内容，服务端节在准备好响应结果后立刻与请求端新建一个rpc长连接/复用已存在的长连接，将响应写回去；" +
                        "2、停服务时本地server会先停止，server停止就会关闭socket和server的io线程池，由于server的io线程池和业务线程池是分离的，" +
                        "业务线程池会继续运行直到所有任务处理完毕为止。此时，本节点不再有能力接收外部请求了，但是：" +
                        "a.在即将停止的节点内，业务线程池任然需要向外部发送请求以完成业务操作，以及得到响应结果，因此client必须保持打开的。" +
                        "b.在即将停止的节点内，业务线程池需要将本地执行结果返回给远程，这时候server已停，无法复用原管道将结果写回去，因此必须使用依然存活的client" +
                        "将结果写回。" +
                        "因此，有如下逻辑：所有server优先复用当前管道将响应写回去，当SERVER关闭后，业务线程池中后续任务通过未停止的client回写响应结果。");
                UnitResponse response = json.toJavaObject(UnitResponse.class);
                String ssid = response.getContext().getSsid();
                ThreadPoolManager.execute(() -> {
                    NotifyHandler callback = LocalNodeManager.handleMap.getIfPresent(ssid);
                    if (callback != null) {
                        LocalNodeManager.handleMap.invalidate(ssid);
                        callback.callback(UnitResponse.create(json));
                    } else {
                        LOG.error(String.format("ssid=%s的消息没有找到对应的notifyHandler!整个消息内容=%s,", ssid, json), new Throwable());
                    }
                }, response.getContext().getMsgId());
            } else
                LOG.error("rpc server端只支持request和response两种消息类型，不支持:" + MessageType.getMessageType(json), new RuntimeException());
        } catch (Throwable e) {
            LOG.error(e);
        } finally {
            MsgIdHolder.clear();
        }

    }

    /**
     * 打印rpc消息传输耗时等信息:术语rpcFly
     *
     * @deprecated 打印日志消耗性能，即使是debug级别也是，因此废弃，请不要再调用本方法
     */
    private void logRpcFly(JSONObject msgJson, String msgStr, ChannelHandlerContext ctx) {
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
                    put("length", msgStr.length());
                    put("payload", msgStr);
                    put("msgType", msgType);
                    put("client", ctx.channel().remoteAddress());
                    put("server", ctx.channel().localAddress());
                }});
            }
        } catch (Throwable e) {
            LOG.error(e);
        }
    }
}
