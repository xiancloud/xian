package info.xiancloud.plugin.mqtt;

import info.xiancloud.plugin.conf.XianConfig;
import info.xiancloud.plugin.message.IdManager;
import info.xiancloud.plugin.util.Pair;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 * @author happyyangyuan
 */
public class MqttClientBuilder {
    private String nodeId = IdManager.LOCAL_NODE_ID;
    private MqttCallbackAdaptor callback = new NonblockingMqttCallBack(null);
    private boolean cleanSession = MqttConnectOptions.CLEAN_SESSION_DEFAULT;
    private int keepAliveInterval = MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT;
    private Integer qos = 0;

    public static MqttClientBuilder newBuilder() {
        return new MqttClientBuilder();
    }

    /**
     * 指定客户端节点id，如果不指定那么使用默认本地节点{@link IdManager#LOCAL_NODE_ID}
     */
    public MqttClientBuilder id(String mqttClientId) {
        this.nodeId = mqttClientId;
        return this;
    }

    /**
     * 指定mqtt消息回调对象，如果不给定，那么使用默认的{@link NonblockingMqttCallBack}
     */
    public MqttClientBuilder callback(MqttCallbackAdaptor callback) {
        this.callback = callback;
        return this;
    }

    /**
     * 指定mqtt cleanSession属性，如果不指定那么使用默认的{@link MqttConnectOptions#CLEAN_SESSION_DEFAULT}
     */
    public MqttClientBuilder cleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
        return this;
    }

    /**
     * 指定mqtt客户端keepAliveInterval,单位秒，如果不指定那么使用默认的{@link MqttConnectOptions#KEEP_ALIVE_INTERVAL_DEFAULT}
     */
    public MqttClientBuilder keepAliveInterval(int keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
        return this;
    }

    /**
     * 不设置，则使用默认qos0
     */
    public MqttClientBuilder qos(int qos) {
        this.qos = qos;
        return this;
    }

    public IMqttPublisher buildPublisher() {
        IMqttPublisher publisher;
        publisher = new DefaultMqttClientAdaptor() {
            @Override
            protected MqttCallbackAdaptor getCallback(IMqttClient owner) {
                return callback.setOwner(owner);
            }

            @Override
            public int getQos() {
                return qos;
            }

            @Override
            public String getMqttClientId() {
                return nodeId;
            }
        };
        publisher.setCleanSession(cleanSession);
        publisher.setKeepAliveInterval(keepAliveInterval);
        return publisher;
    }

    public IMqttConsumer buildConsumer() {
        IMqttConsumer consumer;
        consumer = new DefaultMqttClientAdaptor() {
            @Override
            protected MqttCallbackAdaptor getCallback(IMqttClient owner) {
                return callback.setOwner(owner);
            }

            @Override
            public int getQos() {
                return qos;
            }

            @Override
            public String getMqttClientId() {
                return nodeId;
            }
        };
        consumer.setCleanSession(cleanSession);
        consumer.setKeepAliveInterval(keepAliveInterval);
        return consumer;
    }

    public Pair<IMqttPublisher, IMqttConsumer> buildBoth() {
        IMqttPublisher publisher;
        publisher = new DefaultMqttClientAdaptor() {
            @Override
            protected MqttCallbackAdaptor getCallback(IMqttClient owner) {
                return callback.setOwner(owner);
            }

            @Override
            public int getQos() {
                return qos;
            }

            @Override
            public String getMqttClientId() {
                return nodeId;
            }
        };
        publisher.setCleanSession(cleanSession);
        publisher.setKeepAliveInterval(keepAliveInterval);
        IMqttConsumer consumer = (DefaultMqttClientAdaptor) publisher;
        return Pair.of(publisher, consumer);
    }

    /**
     * 通用的mqtt客户端实现.txt
     *
     * @author happyyangyuan
     */
    private abstract static class DefaultMqttClientAdaptor extends AbstractMqttClient implements IMqttConsumer, IMqttPublisher {

        @Override
        public String wrap(String xianPid) {
            return xianPid;
        }

        @Override
        protected char[] getPwd() {
            return XianConfig.get("rabbitmqPwd").toCharArray();
        }

        @Override
        public String getBroadcastId() {
            return IdManager.getSysBroadcastId();
        }

        @Override
        public String getConsumeId() {
            return getMqttClientId();
        }
    }
}
