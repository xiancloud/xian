package info.xiancloud.plugin.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.rabbitmq.client.*;
import info.xiancloud.plugin.conf.EnvConfig;
import info.xiancloud.plugin.mq.IMqConsumerClient;
import info.xiancloud.plugin.mq.IMqPubClient;
import info.xiancloud.plugin.util.EnvUtil;
import info.xiancloud.plugin.util.LOG;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * 本类为全局单例：发布channel为全局单例，consumer与channel一对一
 *
 * @author happyyangyuan
 */
public class RabbitMqClient implements IMqPubClient, IMqConsumerClient {

    private Connection conn;
    private Channel defaultGlobalPublisherChannel;
    private LoadingCache<String, Channel> consumerChannels = CacheBuilder.newBuilder()
            .build(new CacheLoader<String, Channel>() {
                public Channel load(String queueName) throws Exception {
                    Channel consumerChannel = conn.createChannel();
                    consumerChannel.basicQos(10);
                    return consumerChannel;
                }
            });
    private final String EXCHANGE_NAME = "xian-global-exchange";
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    @Override
    public boolean p2pPublish(String queueName, String payload) {
        return basicPublish(queueName, payload, false);
    }

    @Override
    public boolean staticPublish(String queueName, String payload) {
        return basicPublish(queueName, payload, true);
    }

    private boolean basicPublish(String queueName, String payload, boolean staticQueue) {
        try {
            initIfNotInitialized();
        } catch (Exception e) {
            LOG.error(e);
            return false;
        }
        synchronized (defaultGlobalPublisherChannel) {
            try {
                createQueueIfNotCreated(queueName, staticQueue);
                byte[] messageBodyBytes = payload.getBytes();
                defaultGlobalPublisherChannel.basicPublish(EXCHANGE_NAME, queueName, null, messageBodyBytes);
                return true;
            } catch (Throwable e) {
                LOG.error("发布消息至队列失败：" + queueName, e);
                return false;
            }
        }
    }

    private Cache<String, Boolean> queueBound = CacheBuilder.newBuilder()
            //设置过期时间，保证队列被人意外删除后可以恢复，比如队列被人从rabbitmq后台意外删除掉了
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .build();

    private synchronized void createQueueIfNotCreated(String queueName, boolean staticQueue) throws IOException {
        Boolean bounded = queueBound.getIfPresent(queueName);
        if (bounded == null || !bounded) {
            //rabbitMq允许重复接受队列创建和绑定请求
            defaultGlobalPublisherChannel.queueDeclare(queueName, staticQueue, false, !staticQueue, null);
            defaultGlobalPublisherChannel.queueBind(queueName, EXCHANGE_NAME, queueName);
            queueBound.put(queueName, true);
        }
    }

    @Override
    public void destroy() {
        synchronized (initialized) {
            if (initialized.get()) {
                do_destroy();
                initialized.set(false);
            }
        }
    }

    private void do_destroy() {
        try {
            if (defaultGlobalPublisherChannel != null && defaultGlobalPublisherChannel.isOpen()) {
                defaultGlobalPublisherChannel.close();
            } else {
                LOG.error("channel已关闭，不应当重复关闭");
            }
            if (conn != null && conn.isOpen()) {
                conn.close();
            } else {
                LOG.error("connection已关闭，不应当重复关闭");
            }
            queueBound.invalidateAll();
        } catch (Throwable e) {
            LOG.error(e);
        }
    }

    public void initIfNotInitialized() throws Exception {
        if (!initialized.get())
            synchronized (initialized) {
                if (!initialized.get()) {
                    do_init();
                    initialized.set(true);
                }
            }
    }

    private void do_init() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(EnvConfig.get("rabbitUserName"));
        factory.setPassword(EnvConfig.get("rabbitPwd"));
        factory.setVirtualHost("/");
        if (EnvUtil.isLan())
            factory.setHost(EnvConfig.get("rabbitLanHost"));
        else
            factory.setHost(EnvConfig.get("rabbitInternetHost"));
        factory.setPort(EnvConfig.getIntValue("rabbitPort"));
        factory.setAutomaticRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(5000);
        try {
            conn = factory.newConnection();
            defaultGlobalPublisherChannel = conn.createChannel();
            defaultGlobalPublisherChannel.exchangeDeclare(EXCHANGE_NAME, "direct", true);//支持重复declare
        } catch (IOException | TimeoutException e) {
            throw new Exception("初始化rabbit连接失败", e);
        }
    }

    private boolean basicConsume(String queueName, Function<JSONObject, Boolean> function, boolean staticQueue) {
        try {
            initIfNotInitialized();
        } catch (Exception e) {
            LOG.error(e);
            return false;
        }
        Channel channel = consumerChannels.getUnchecked(queueName);
        synchronized (channel) {
            try {
                createQueueIfNotCreated(queueName, staticQueue);
                channel.basicConsume(queueName, false, queueName,
                        new DefaultConsumer(channel) {
                            @Override
                            public void handleDelivery(String consumerTag,
                                                       Envelope envelope,
                                                       AMQP.BasicProperties properties,
                                                       byte[] body)
                                    throws IOException {
                                long deliveryTag = envelope.getDeliveryTag();
                                ackNoException(channel, function.apply((JSONObject) JSON.parse(body)), deliveryTag);
                            }
                        });
                return true;
            } catch (Throwable e) {
                LOG.error("订阅队列失败：" + queueName, e);
                return false;
            }
        }
    }

    /**
     * ack，如果出现异常，那么只打印日志不抛出异常
     */
    private boolean ackNoException(Channel consumerChannel, boolean msgApplied, final long deliveryTag) {
        try {
            if (msgApplied) {
                //设置multiple为true，经过压测发现有一定几率ack出现ioException的情况，这里顺带把前面的消息一起ack掉
                consumerChannel.basicAck(deliveryTag, true);
            } else {
                consumerChannel.basicNack(deliveryTag, false, true);
            }
            return true;
        } catch (Throwable unknownException) {
            LOG.error(unknownException);
            return false;
        }
    }

    @Override
    public boolean consumeStaticQueue(String queueName, Function<JSONObject, Boolean> function) {
        return basicConsume(queueName, function, true);
    }

    public boolean consumeNonStaticQueue(String queueName, Function<JSONObject, Boolean> function) {
        return basicConsume(queueName, function, false);
    }

    @Override
    public boolean unconsume(String queueName) {
        Channel consumerChannel = consumerChannels.getIfPresent(queueName);
        if (consumerChannel != null)
            synchronized (consumerChannel) {
                try {
                    consumerChannel.basicCancel(queueName);
                    consumerChannels.invalidate(queueName);
                    consumerChannel.close();
                    return true;
                } catch (IOException | TimeoutException e) {
                    LOG.error("取消订阅失败：" + queueName, e);
                    return false;
                }
            }
        else {
            LOG.error(new Throwable("rabbitmq channel未初始化，不执行取订动作：" + queueName));
            return false;
        }

    }

    @Override
    public boolean pullStaticQueue(String queueName, Function<JSONObject, Boolean> function) {
        return basicGet(queueName, function, true);
    }

    private boolean basicGet(String queueName, Function<JSONObject, Boolean> function, boolean staticQueue) {
        try {
            initIfNotInitialized();
        } catch (Exception e) {
            LOG.error(e);
            return false;
        }
        final Channel consumerChannel = consumerChannels.getUnchecked(queueName);
        synchronized (consumerChannel) {
            try {
                createQueueIfNotCreated(queueName, staticQueue);
                GetResponse response = consumerChannel.basicGet(queueName, false);
                if (response == null) {
                    // No message retrieved. 表示队列为空，暂时没消息可取，function的实现最好是阻塞一定时间再执行下一步
                    Thread.sleep(1000);
                } else {
                    long deliveryTag = response.getEnvelope().getDeliveryTag();
                    byte[] body = response.getBody();
                    ackNoException(consumerChannel, function.apply((JSONObject) JSON.parse(body)), deliveryTag);
                }
                return true;
            } catch (Throwable e) {
                LOG.error("拉取队列消息失败：" + queueName, e);
                return false;
            }
        }
    }

    @Override
    public boolean pullNonStaticQueue(String queueName, Function<JSONObject, Boolean> function) {
        return basicGet(queueName, function, false);
    }
}
