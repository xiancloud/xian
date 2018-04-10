package info.xiancloud.yy.port;


import info.xiancloud.core.distribution.Node;

import java.io.IOException;

/**
 * @author happyyangyuan
 */
public class TestGetRandomPort {
    public static void main(String[] args) throws IOException, InterruptedException {
//        System.out.println(new ServerSocket(0).getLocalPort());
        System.out.println(Node.RPC_PORT);
        Thread.sleep(Long.MAX_VALUE);
    }
}
