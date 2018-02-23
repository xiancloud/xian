package info.xiancloud.plugin.mqtt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import info.xiancloud.plugin.conf.EnvConfig;
import info.xiancloud.plugin.util.EnvUtil;
import info.xiancloud.plugin.util.LOG;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * 单例的mqtt客户端
 *
 * @author happyyangyuan
 */
public abstract class AbstractMqttClient implements IMqttClient {

    @JSONField(serialize = false, deserialize = false)
    private MqttAsyncClient sampleClient;
    private String[] serverURIs = EnvUtil.isLan() ? EnvConfig.getStringArray("rabbitmqLanServerUrls") :
            EnvConfig.getStringArray("rabbitmqInternetServerUrls");
    private String userName = EnvConfig.get("rabbitmqUserName");
    private MqttConnectOptions connOpts;
    private MemoryPersistence persistence = new MemoryPersistence();
    private Boolean cleanSession = true;
    private int keepAliveInterval = MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT;

    protected abstract char[] getPwd();

    /**
     * 连接mqtt server,并返回一个客户端对象,如果连接失败,那么返回null
     */
    public MqttAsyncClient connectBroker() {
        LOG.info(String.format("mqtt=======客户端%s与rabbitMQ server: %s 准备建立连接,userName = %s", getMqttClientId(), JSON.toJSONString(serverURIs), userName));
        try {
            sampleClient = new MqttAsyncClient("tcp://overriddenByMqttConnectOptions.setServerURIs:1883", getMqttClientId(), persistence);
            connOpts = new MqttConnectOptions();
            connOpts.setAutomaticReconnect(true);
            connOpts.setServerURIs(serverURIs);
            connOpts.setUserName(userName);
            connOpts.setPassword(getPwd());
            connOpts.setCleanSession(cleanSession);
            connOpts.setMaxInflight(1000 /**默认的值是10,对于我们来说这个值太小!*/);
            connOpts.setKeepAliveInterval(keepAliveInterval);
            sampleClient.setCallback(getCallback(this));
            sampleClient.connect(connOpts).waitForCompletion(60 * 1000);
            LOG.info(String.format("mqtt=======客户端%s与rabbitMQ server: %s 建立连接完成,userName = %s", getMqttClientId(), JSON.toJSONString(serverURIs), userName));
            return sampleClient;
        } catch (MqttException me) {
            throw new RuntimeException(String.format("mqtt=======客户端%s与rabbitMQ server: %s 连接失败!!! userName = %s", getMqttClientId(), JSON.toJSONString(serverURIs), userName), me);
        }
    }

    abstract protected MqttCallbackAdaptor getCallback(IMqttClient owner);

    /*
    private final Object reconnectionLock = new Object();

    public void reconnect() {
        if (lastReconnTime == null || System.currentTimeMillis() - lastReconnTime.getTime() > RECONN_INTERVAL_IN_MILLI) {
            LOG.info("[MQTT] 重连...重连...重连...重连...重连...重连...重连...重连...重连...重连...重连...重连...重连...重连...重连...重连...重连...");
            try {
                if (!sampleClient.isConnected()) {
                    synchronized (reconnectionLock) {
                        if (!sampleClient.isConnected()) {
                            LOG.debug("eclipse-paho-mqtt-1.1.0内置的重连机制有问题！真是傻叉！不得已只能强制关闭再建立连接");
                            sampleClient.reconnect();
                            if (this instanceof IMqttConsumer && isCleanSession()) {
                                LOG.debug("如果设置setCleanSession=false，队列是被持久化到rabbitServer的硬盘的，据api文档所述，客户端重连是可以恢复订阅关系的，" +
                                        "但是我们以后可能会setCleanSession=true的，因此我们依然是需要重新订阅以确保订阅关系时100%存在的。");
                                IMqttConsumer consumer = (IMqttConsumer) this;
                                consumer.consumeAll();
                            }
                            LOG.info("[MQTT]重连完毕!重连完毕!重连完毕!重连完毕!重连完毕!重连完毕!重连完毕!重连完毕!重连完毕!重连完毕!重连完毕!重连完毕!重连完毕!重连完毕!");
                        } else {
                            LOG.info("[MQTT]已经连接");
                        }
                    }
                } else {
                    LOG.error("已经处于连接状态了，不需要再次重连", new Throwable());
                }
            } catch (MqttException e) {
                LOG.error("真可惜MQTT重连失败，请下次再来！", e);
            } catch (Throwable other) {
                LOG.error(other);
            } finally {
                lastReconnTime = new Date();
            }
        } else {
            LOG.warn("重连过于频繁，拒绝本次重连请求. 默认的重连时间间隔必须大于（毫秒）:" + RECONN_INTERVAL_IN_MILLI);
        }
    }

    private volatile static Date lastReconnTime;
    private static final long DISCONN_TIMEOUT_IN_MILLI = 2000;
    private static final long CONN_TIMEOUT_IN_MILLI = 2000;
    private static final long RECONN_INTERVAL_IN_MILLI = 1000;
    */

    @Override
    public MqttAsyncClient getSampleClient() {
        return sampleClient;
    }

    synchronized public void disconnect() {
        try {
            if (sampleClient != null) {
                sampleClient.disconnect();
            }
        } catch (Throwable e) {
            try {
                sampleClient.disconnectForcibly();
            } catch (MqttException e1) {
                LOG.error(e);
            }
        }
    }

    @Override
    public boolean isCleanSession() {
        return cleanSession;
    }

    public AbstractMqttClient setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
        return this;
    }

    @Override
    public int getKeepAliveInterval() {
        return keepAliveInterval;
    }

    @Override
    public AbstractMqttClient setKeepAliveInterval(int keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
        return this;
    }
}