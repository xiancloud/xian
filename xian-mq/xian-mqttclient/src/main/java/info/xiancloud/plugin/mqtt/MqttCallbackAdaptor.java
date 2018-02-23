package info.xiancloud.plugin.mqtt;

import info.xiancloud.plugin.util.LOG;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;

/**
 * @author happyyangyuan
 */
public abstract class MqttCallbackAdaptor implements MqttCallbackExtended {
    /**
     * 回调的属主
     */
    private IMqttClient owner;

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        if (reconnect) {
            LOG.info("[MQTT自动重连] 重连完毕!重连完毕!重连完毕!重连完毕!重连完毕!重连完毕!重连完毕!重连完毕!重连完毕!重连完毕!重连完毕!重连完毕!重连完毕!");
        } else {
            LOG.info("[MQTT]连接完毕!连接完毕!连接完毕!连接完毕!连接完毕!连接完毕!连接完毕!连接完毕!连接完毕!连接完毕!连接完毕!连接完毕!连接完毕!连接完毕!");
        }
    }

    @Override
    public void connectionLost(Throwable e) {
        LOG.error(e);
    }

    public IMqttClient getOwner() {
        return owner;
    }

    public MqttCallbackAdaptor setOwner(IMqttClient owner) {
        this.owner = owner;
        return this;
    }

}
