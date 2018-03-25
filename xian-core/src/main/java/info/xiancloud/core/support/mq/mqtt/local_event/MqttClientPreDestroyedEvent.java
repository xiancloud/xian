package info.xiancloud.core.support.mq.mqtt.local_event;

import info.xiancloud.core.distribution.Node;
import info.xiancloud.core.distribution.Node;

/**
 * mqttClientNode在准备销毁时会发布该事件，目前用于发起"停止mqtt广播"
 *
 * @author happyyangyuan
 * @deprecated mqtt is deprecated for rpc
 */
public class MqttClientPreDestroyedEvent extends AbstractMqttClientNodeStatusChangedEvent {
    public MqttClientPreDestroyedEvent(Node mqttClientNode) {
        super(mqttClientNode);
    }
}
