package info.xiancloud.plugin.mqtt;

import info.xiancloud.plugin.util.LOG;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * @author happyyangyuan
 */
public interface IMqttConsumer extends IMqttClient {

    default void consumeAll() {
        consume(getConsumeId(), getQos());
        /*String mqttNodeName = NodeIdBean.parse(getMqttClientId()).getApplication();
        不再订阅广播了
        if (!TransferQueueUtil.isTransferApplicationName(mqttNodeName)) {
            consume(getBroadcastId(), 0);
        } else {
            LOG.info("说明:transfer节点不订阅广播!");
        }*/
    }

    default void unconsumeAll() {
        unconsume(getConsumeId());
    }

    String getConsumeId();

    default void consume(String multiLevelTopic, int qos) {
        LOG.info("___________________订阅" + multiLevelTopic);
        try {
            getSampleClient().subscribe(multiLevelTopic, qos).waitForCompletion(1000 * 20);
        } catch (MqttException e) {
            throw new RuntimeException("[MQTT] 订阅" + multiLevelTopic + "失败!", e);
        }
    }

    default void unconsume(String multiLevelTopic) {
        LOG.info("___________________取消订阅" + multiLevelTopic);
        try {
            getSampleClient().unsubscribe(multiLevelTopic).waitForCompletion(1000 * 20);
        } catch (MqttException e) {
            throw new RuntimeException("[MQTT] 取消订阅" + multiLevelTopic + "失败!", e);
        }
    }
}
