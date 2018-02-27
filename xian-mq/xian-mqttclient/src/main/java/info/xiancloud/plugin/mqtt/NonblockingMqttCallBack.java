package info.xiancloud.plugin.mqtt;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.Constant;
import info.xiancloud.plugin.distribution.LocalNodeManager;
import info.xiancloud.plugin.distribution.MessageType;
import info.xiancloud.plugin.message.IdManager;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.sender.local.DefaultLocalAsyncSender;
import info.xiancloud.plugin.support.mq.mqtt.handle.NotifyHandler;
import info.xiancloud.plugin.support.mq.mqtt.mqtt_callback.sequencer.ISequencer;
import info.xiancloud.plugin.thread_pool.ThreadPoolManager;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.Reflection;
import info.xiancloud.plugin.util.thread.MsgIdHolder;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.RejectedExecutionException;

/**
 * @author happyyangyuan
 * @deprecated mqtt is not used any more.
 */
public class NonblockingMqttCallBack extends MqttCallbackAdaptor {

    public NonblockingMqttCallBack(IMqttClient owner) {
        setOwner(owner);
    }

    @Override
    public void messageArrived(String topicIgnored, MqttMessage message) throws Exception {
        try {
            /*第一个参数如果包含"."会被eclipse paho转为"/",所以,不要依赖这个参数!*/
            String msgFullStr = message.toString();
            JSONObject json = JSONObject.parseObject(msgFullStr);
            IdManager.makeSureMsgId(json);
            MessageType messageType = MessageType.getMessageType(json);
            switch (messageType) {
                case offline:
                    LOG.info(String.format("离线广播: %s", json));
                    break;
                case request:
                    UnitRequest request = Reflection.toType(json, UnitRequest.class);
                    logMqttFly(request, msgFullStr);
                    String group = request.getContext().getGroup(),
                            unit = request.getContext().getUnit();
                    request.getContext().setFromRemote(true);
                    ISequencer.build(group, unit, json).sequence(
                            //run this runnable if succeeded.
                            () -> new DefaultLocalAsyncSender(request, new NotifyHandler() {
                                protected void handle(UnitResponse unitResponse) {
                                    LocalNodeManager.sendBack(unitResponse);
                                }
                            }).send(),
                            /*failed directly, call this handler*/
                            new NotifyHandler() {
                                protected void handle(UnitResponse unitResponse) {
                                    LocalNodeManager.sendBack(unitResponse);
                                }
                            });
                    break;
                case response:
                    LOG.debug("ssid只在远程请求和响应时才会有值");
                    UnitResponse response = Reflection.toType(json, UnitResponse.class);
                    logMqttFly(response, msgFullStr);
                    String ssid = response.getContext().getSsid();
                    NotifyHandler handler = LocalNodeManager.handleMap.getIfPresent(ssid);
                    LocalNodeManager.handleMap.invalidate(ssid);
                    UnitResponse responseUnitResponse = UnitResponse.create(json);
                    if (handler == null) {
                        LOG.error(String.format("ssid=%s的消息没有找到对应的notifyHandler!整个消息内容=%s,", ssid, json), new Throwable());
                        break;
                    }
                    try {
                        ThreadPoolManager.execute(() -> {
                            handler.callback(responseUnitResponse);
                        });
                    } catch (RejectedExecutionException threadPoolAlreadyShutdown) {
                        LOG.info("线程池已关闭，这里使用临时线程执行任务，针对停服务时线程池已关闭的情况。");
                        new Thread(() -> handler.callback(responseUnitResponse)).start();
                    }
                    break;
                default:
                    LOG.error("未知的mqtt消息类型:" + messageType, new RuntimeException());
            }
        } catch (Throwable e) {
            LOG.error(e);
        } finally {
            MsgIdHolder.clear();
        }
    }

    /**
     * 打印mqtt消息传输耗时等信息:术语mqttFly
     */
    private void logMqttFly(UnitRequest request, String msgStr) {
        long start = request.getContext().getSentTimestamp();
        LOG.debug(new JSONObject() {{
            put(Constant.COST, System.currentTimeMillis() - start);
            put("type", "mqttFly");//从发出到收到，主要用于监控内网间通信性能
            put("ssid", request.getContext().getSsid());
            put("from", request.getContext().getSourceNodeId());
            put("length", msgStr.length());
            put("msgType", request.getContext().getMessageType());
            put("payload", msgStr);
        }});
    }

    private void logMqttFly(UnitResponse response, String msgStr) {
        long start = response.getContext().getSentTimestamp();
        LOG.debug(new JSONObject() {{
            put(Constant.COST, System.currentTimeMillis() - start);
            put("type", "mqttFly");//从发出到收到，主要用于监控内网间通信性能
            put("ssid", response.getContext().getSsid());
            put("from", response.getContext().getSourceNodeId());
            put("length", msgStr.length());
            put("msgType", response.getContext().getMessageType());
            put("payload", msgStr);
        }});
    }

}
