import info.xiancloud.plugin.distribution.LocalNodeManager;
import info.xiancloud.plugin.message.IdManager;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.mqtt.IMqttPublisher;
import info.xiancloud.plugin.mqtt.MqttCallbackAdaptor;
import info.xiancloud.plugin.mqtt.MqttClientBuilder;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.After;
import org.junit.Test;

/**
 * @author happyyangyuan
 */
public class MqttKeepAlive0Test {

    private IMqttPublisher node;

    @After
    public void stop() throws InterruptedException {
        node.p2pPublish(LocalNodeManager.LOCAL_NODE_ID, "这个消息");
        Thread.sleep(1000 * 600);
        node.disconnect();
    }

    @Test
    public void testKeepAlive0() {
        MqttCallbackAdaptor callbackAdaptor = new MqttCallbackAdaptor() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                LOG.info(topic);
                LOG.info(message);
                Thread.sleep(Long.MAX_VALUE);
            }
        };
        String queue = IdManager.generateStaticQueueId("happyyangyuan-test-blocking-queue");
        node = MqttClientBuilder
                .newBuilder()
                .id(queue)
                .callback(callbackAdaptor)
                .cleanSession(true)
                .qos(0)
                .keepAliveInterval(0)
                //设置为0表示禁用keepAlive，因为我们会在上游节点不可达时阻塞mqtt消息队列.
                //这里验证关闭keepAlive是否会被server断开连接
                .buildPublisher();
        node.connectBroker();
    }
}
