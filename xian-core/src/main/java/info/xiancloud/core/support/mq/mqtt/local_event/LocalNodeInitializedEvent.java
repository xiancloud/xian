package info.xiancloud.core.support.mq.mqtt.local_event;

import info.xiancloud.core.distribution.Node;
import info.xiancloud.core.distribution.Node;

/**
 * 节点实例初始化完毕事件
 *
 * @author happyyangyuan
 * @deprecated 跳来跳去的，看着就烦
 */
public class LocalNodeInitializedEvent extends AbstractMqttClientNodeStatusChangedEvent {
    public LocalNodeInitializedEvent(Node mqttClientNode) {
        super(mqttClientNode);
    }
}
