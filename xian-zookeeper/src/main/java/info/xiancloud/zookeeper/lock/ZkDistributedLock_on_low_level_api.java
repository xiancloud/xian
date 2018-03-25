package info.xiancloud.zookeeper.lock;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * 基于zookeeper底层api实现的分布式锁
 *
 * @author happyyangyuan
 * @deprecated 1、未经过测试  2、不要重复造轮子!
 */
public class ZkDistributedLock_on_low_level_api {
    private final ZooKeeper zk;
    private final String lockBasePath;
    private final String lockName;
    private String lockPath;

    public ZkDistributedLock_on_low_level_api(ZooKeeper zk, String lockBasePath, String lockName) {
        if (lockName.contains("/")) {
            throw new RuntimeException("锁名称不允许包含'/'");
        }
        this.zk = zk;
        this.lockBasePath = lockBasePath;
        this.lockName = lockName;
    }

    public void lock() throws IOException {
        try {
            // lockPath will be different than 'lockBasePath/lockName' because of the sequence number ZooKeeper appends
            lockPath = zk.create(lockBasePath + "/" + lockName, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            final Object lock = new Object();
            synchronized (lock) {
                while (true) {
                    List<String> nodes = zk.getChildren(lockBasePath, event -> {
                        if (Watcher.Event.EventType.NodeDeleted == event.getType()
                                && event.getPath().startsWith(lockBasePath + "/" + lockName)
                                ) {
                            synchronized (lock) {
                                lock.notifyAll();
                            }
                        }
                    });
                    Collections.sort(nodes); // ZooKeeper node names can be sorted lexicographically(字典顺序)
                    if (lockPath.endsWith(nodes.get(0))) {
                        return;
                    } else {
                        lock.wait();
                    }
                }
            }
        } catch (KeeperException | InterruptedException e) {
            throw new IOException(e);
        }
    }

    public void unlock() throws IOException {
        try {
            zk.delete(lockPath, -1);
            lockPath = null;
        } catch (KeeperException | InterruptedException e) {
            throw new IOException(e);
        }
    }

    public static void main(String... args) throws InterruptedException {
        final Object lock = new Object();
        synchronized (lock) {
            new Thread(() -> {
                synchronized (lock) {
                    System.out.println("lock2");
                    lock.notifyAll();
                }
            }).start();
            Thread.sleep(1000);
            lock.wait();
            System.out.println("lock1");
        }
    }
}
