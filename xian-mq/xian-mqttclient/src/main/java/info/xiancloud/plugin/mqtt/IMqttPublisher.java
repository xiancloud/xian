package info.xiancloud.plugin.mqtt;

import info.xiancloud.plugin.util.LOG;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @author happyyangyuan
 */
public interface IMqttPublisher extends IMqttClient/*, IMsgPublisher*/ {

    //点对点发布消息,返回一个callback
    default boolean p2pPublish(String xianPid, String payload) {
        LOG.debug("MQ发送消息>>> " + xianPid + " >>> " + payload);
        final long start = System.nanoTime();
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(getQos());
        message.setRetained(false);
        String wrappedTargetId = wrap(xianPid);
        boolean msgPublished = false;
        LOG.debug("原来重发次数是5，现改为fast-fail模式，不再重试,直接返回失败给上层应用,优先保证服务器不会过载");
        short count = 0, MAX_RETRY = 0;
        do {
            try {
                msgPublished = true;
                count++;
                getSampleClient().publish(wrappedTargetId, message).setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        if (System.nanoTime() - start > 1000000 * 1000)
                            LOG.cost("mqttPub", start, System.nanoTime());
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        LOG.error(exception);
                        LOG.cost("mqttPub", start, System.nanoTime());
                    }
                });
            } catch (MqttException e) {
                LOG.error(e);
                if (MqttException.REASON_CODE_CLIENT_NOT_CONNECTED == e.getReasonCode()) {
                    LOG.debug("注意：这里只针对客户端未连接这一种异常设置消息重发，其他不重发！");
                    msgPublished = false;
                    /*reconnect();*/
                }
            }
        } while (!msgPublished && count < MAX_RETRY);
        return true;
    }

    String wrap(String xianPid);


}
