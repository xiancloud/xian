package info.xiancloud.yy.netty_httpserver;

import info.xiancloud.nettyhttpserver.NettyServer;

/**
 * @author happyyangyuan
 */
public class TestStartupNettyHttpServer {
    public static void main(String[] args) throws InterruptedException {
        NettyServer server = new NettyServer();
        server.startServer();
        Thread.sleep(10000);
        server.stopServer();
    }
}
