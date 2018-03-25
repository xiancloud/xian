package info.xiancloud.rabbitmqclient;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import info.xiancloud.core.conf.XianConfig;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author happyyangyuan
 */
public class RabbitMqClientDemo {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(XianConfig.get("rabbitmqUserName"));
        factory.setPassword(XianConfig.get("rabbitmqPwd"));
        factory.setVirtualHost("/");
        factory.setHost("production-internet-mq.apaycloud.com");
        factory.setPort(5672);
        Connection conn = factory.newConnection();
        Channel channel = conn.createChannel();


        String exchangeName = "yy-exchange";
        String routingKey = "yy-routingKey";
        String queueName = "yy-queueName";

        channel.exchangeDeclare(exchangeName, "direct", true);
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);

        byte[] messageBodyBytes = "Hello, world2!".getBytes();
        channel.basicPublish(exchangeName, routingKey, null, messageBodyBytes);


        Thread.sleep(1000 * 60);

        channel.close();
        conn.close();
    }
}
