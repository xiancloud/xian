package org.graylog2;

import info.xiancloud.plugin.util.RetryUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class GelfUDPSender implements GelfSender {

    private InetAddress host;
    private int port;
    private DatagramChannel channel;

    private static final int MAX_RETRIES = 5;

    public GelfUDPSender() {
    }

    public GelfUDPSender(String host) throws IOException {
        this(host, DEFAULT_PORT);
    }

    public static void main(String[] args) {
        try {
            new GelfUDPSender("fff.ff.xx00", 100);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GelfUDPSender(String host, int port) throws IOException {
        try {
            //由于这里graylog客户端还未初始化，因此无法打印log4j日志，这里输出到标准输出
            //腾讯云容器服务/云解析坑爹玩意儿，概率性遇到域名解析失败，因此这里失败重试三次
            this.host = RetryUtil.retryUntilNoException(() -> InetAddress.getByName(host), 3);
        } catch (Throwable t) {
            if (t instanceof IOException) {
                throw (IOException) t;
            }
            throw new RuntimeException(t);
        }
        this.port = port;
        setChannel(initiateChannel());
    }

    private DatagramChannel initiateChannel() throws IOException {
        DatagramChannel resultingChannel = DatagramChannel.open();
        resultingChannel.socket().bind(new InetSocketAddress(0));
        resultingChannel.connect(new InetSocketAddress(this.host, this.port));
        resultingChannel.configureBlocking(false);

        return resultingChannel;
    }

    public GelfSenderResult sendMessage(GelfMessage message) {
        if (!message.isValid()) return GelfSenderResult.MESSAGE_NOT_VALID;
        return sendDatagrams(message.toUDPBuffers());
    }

    private GelfSenderResult sendDatagrams(ByteBuffer[] bytesList) {

        int tries = 0;
        Exception lastException = null;
        do {

            try {

                if (!getChannel().isOpen()) {
                    setChannel(initiateChannel());
                }

                for (ByteBuffer buffer : bytesList) {
                    getChannel().write(buffer);
                }

                return GelfSenderResult.OK;
            } catch (IOException e) {
                tries++;
                lastException = e;
            }
        } while (tries <= MAX_RETRIES);

        return new GelfSenderResult(GelfSenderResult.ERROR_CODE, lastException);
    }

    public void close() {
        try {
            getChannel().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DatagramChannel getChannel() {
        return channel;
    }

    public void setChannel(DatagramChannel channel) {
        this.channel = channel;
    }
}
