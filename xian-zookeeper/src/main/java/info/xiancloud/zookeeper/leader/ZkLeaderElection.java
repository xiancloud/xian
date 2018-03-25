package info.xiancloud.zookeeper.leader;

import info.xiancloud.core.util.LOG;
import info.xiancloud.zookeeper.ZkConnection;
import info.xiancloud.zookeeper.ZkPathManager;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;

/**
 * 集群选主,本选主机制不会自动触发，请按需手动触发
 *
 * @author happyyangyuan
 */
public class ZkLeaderElection {
    private static ZkLeaderElection singleton;
    private static final Object lock = new Object();
    private LeaderSelector leaderSelector;

    /**
     * 启动主节点选举
     */
    public static void start() {
        synchronized (lock) {
            if (singleton != null) return;
            ZkLeaderElection leaderElection = new ZkLeaderElection();
            LeaderSelectorListener listener = new LeaderSelectorListenerAdapter() {
                public void takeLeadership(CuratorFramework client) throws InterruptedException {
                    LOG.info("被提升为主节点！");
                    try {
                        Thread.sleep(Long.MAX_VALUE);
                    } catch (InterruptedException e) {
                        LOG.info("节点主动断开 or  主动让出主节点身份？反正此处是预期的打断！不需要打印堆栈");
                    } finally {
                        LOG.info("主节点降级为普通节点！");
                    }
                }
            };
            leaderElection.leaderSelector = new LeaderSelector(ZkConnection.client, ZkPathManager.getMyNodeBasePath(), listener);
            leaderElection.leaderSelector.autoRequeue(); //not required, but this is behavior that you will probably expect
            leaderElection.leaderSelector.start();
            singleton = leaderElection;
            LOG.info("ZkLeaderElection启动完毕.");
        }
    }

    public static boolean isLeader() {
        LeaderSelector leaderSelector = singleton.leaderSelector;
        if (leaderSelector != null) {
            return leaderSelector.hasLeadership();
        }
        LOG.warn("当前节点根本就没有参与选举，你为何要检查它是否是leader呢？", new Throwable());
        return false;
    }

    /**
     * 如果参与了选举，那么退出主节点选举
     *
     * @deprecated curator的选举算法有问题，在最后一个唯一节点，同时也是主节点退出选举时，它抛出java.lang.InterruptedException。
     * 所以请直接依赖zk断开连接的方式退出节点选举，而不是调用本方法来退出选举
     */
    public static void stop() {
        synchronized (lock) {
            if (singleton == null) return;
            LeaderSelector leaderSelector = singleton.leaderSelector;
            if (leaderSelector == null) {
                return;
            }
            LOG.info("节点退出zk选举");
            leaderSelector.close();
            singleton = null;
            LOG.info("退出选举 完毕");
        }
    }
}
