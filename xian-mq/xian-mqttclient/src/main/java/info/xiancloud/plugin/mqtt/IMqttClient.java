package info.xiancloud.plugin.mqtt;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

/**
 * @author happyyangyuan
 */
public interface IMqttClient {

    MqttAsyncClient connectBroker();

    void disconnect();

    /**
     * 要求实现为fail-fast重连方式，失败即放弃，不阻塞不重试
     */
    /*void reconnect(); 废弃重连*/

    int getQos();

    MqttAsyncClient getSampleClient();

    String getBroadcastId();

    String getMqttClientId();

    boolean isCleanSession();

    IMqttClient setCleanSession(boolean cleanSession);

    int getKeepAliveInterval();

    IMqttClient setKeepAliveInterval(int keepAliveInterval);
}
