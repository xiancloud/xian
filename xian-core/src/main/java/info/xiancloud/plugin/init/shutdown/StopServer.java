package info.xiancloud.plugin.init.shutdown;

import info.xiancloud.plugin.init.shutdown.shutdown_strategy.ShutdownPort;
import info.xiancloud.plugin.util.LOG;

import java.io.IOException;

/**
 * stop 应用节点进程的main方法入口
 *
 * @author happyyangyuan
 * @deprecated 已经改为使用操作系统级别向pid发送sigterm方式停服务了，因此不再需要这个入口了
 */
public class StopServer {

    public static void main(String[] args) {
        LOG.info("准备shutdown服务器...");
        ShutdownPort.shutdown();
    }

}
