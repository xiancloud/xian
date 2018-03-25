package info.xiancloud.zookeeper;

import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.distribution.IRegistry;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.LOG;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 单例；
 * 将启动项设置为xian-core内的标准化抽象类，然后由zk插件实现该抽象类；不仅可以解决顺序问题，而且可以解决zk的侵入性
 *
 * @author happyyangyuan
 */
public class ZkConnection implements IRegistry {

    public static CuratorFramework client;//全局单例zk客户端
    /**
     * 由于这个状态局部性太强，不具有全局参考价值，因此不建议使用
     */
    private static AtomicBoolean connected = new AtomicBoolean(false);
    private static final Object zkConnectionStartStopLock = new Object();

    public static boolean isConnected() {
        return connected.get();
    }

    public static void start() {
        start(getZkConnStr());
    }

    //仅供内部测试使用
    public static void start(String connectionStr) {
        synchronized (zkConnectionStartStopLock) {
            if (connected.get()) {
                LOG.info("zkConnection已经启动，不再重复启动");
                return;
            }
            try {
                RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
                client = CuratorFrameworkFactory.newClient(connectionStr, retryPolicy);
                client.start();
                LOG.info("阻塞直到与zookeeper连接建立完毕！");
                client.blockUntilConnected();
            } catch (Throwable e) {
                LOG.error(e);
            } finally {
                connected.set(true);
            }
        }
    }

    public static void close() {
        synchronized (zkConnectionStartStopLock) {
            if (connected.get()) {
                try {
                    client.close();
                } catch (Throwable e) {
                    LOG.error(e);
                } finally {
                    connected.set(false);
                }
            } else {
                LOG.warn("zkConnection已经关闭，不再重复关闭");
            }
        }
    }

    private static String getZkConnStr() {
        if (EnvUtil.isLan()) {
            return XianConfig.get("zookeeperConnectionStringLan");
        }
        return XianConfig.get("zookeeperConnectionStringInternet");
    }

    @Override
    public void init() {
        start();
    }

    @Override
    public void destroy() {
        close();
    }
}
