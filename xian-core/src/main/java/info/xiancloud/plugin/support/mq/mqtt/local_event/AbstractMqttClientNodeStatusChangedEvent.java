package info.xiancloud.plugin.support.mq.mqtt.local_event;

import info.xiancloud.plugin.distribution.Node;

/**
 * @author happyyangyuan
 */
public abstract class AbstractMqttClientNodeStatusChangedEvent {
    private Node mqttClientNode;

    public AbstractMqttClientNodeStatusChangedEvent(Node mqttClientNode) {
        this.mqttClientNode = mqttClientNode;
    }

    public Node getMqttClientNode() {
        return mqttClientNode;
    }

    public void setMqttClientNode(Node mqttClientNode) {
        this.mqttClientNode = mqttClientNode;
    }
}
