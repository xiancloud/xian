package org.graylog2.pool;

import info.xiancloud.plugin.util.EnvUtil;
import info.xiancloud.plugin.util.LOG;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Random;

/**
 * @author happyyangyuan
 * @deprecated udp不需要连接池，我已经验证
 */
public class GraylogUdpConnPool {
    private static GraylogUdpConnPool singleton;
    private InetAddress host;
    private static final int poolSize = 10;
    private DatagramChannel[] channels = new DatagramChannel[poolSize];
    int port = 12201;

    public static void init() {
        if (EnvUtil.isQcloudLan()) {
            singleton = new GraylogUdpConnPool("10.66.181.11");
        } else {
            singleton = new GraylogUdpConnPool("log.xiancloud.cn");
        }
    }

    public static void destroy() {
        if (singleton != null) {
            for (DatagramChannel channel : singleton.channels) {
                try {
                    channel.close();
                } catch (IOException e) {
                    LOG.error(e);
                }
            }
        } else {
            LOG.warn("没有开启，不需要销毁");
        }
    }

    public static DatagramChannel getChannel() {
        Random random = new Random();
        return singleton.channels[random.nextInt(poolSize)];
    }

    private GraylogUdpConnPool(String host) {
        System.out.println("poolSie = " + poolSize);
        try {
            this.host = InetAddress.getByName(host);
            for (int i = 0; i < channels.length; i++) {
                channels[i] = initiateChannel();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private DatagramChannel initiateChannel() throws IOException {
        DatagramChannel resultingChannel = DatagramChannel.open();
        resultingChannel.socket().bind(new InetSocketAddress(0));
        resultingChannel.connect(new InetSocketAddress(this.host, port));
        resultingChannel.configureBlocking(false);
        return resultingChannel;
    }

}
