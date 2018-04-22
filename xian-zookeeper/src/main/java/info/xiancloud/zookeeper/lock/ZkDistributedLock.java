package info.xiancloud.zookeeper.lock;


import info.xiancloud.core.util.DateUtil;
import info.xiancloud.core.util.LOG;
import info.xiancloud.zookeeper.ZkConnection;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static info.xiancloud.zookeeper.ZkPathManager.LOCK_ROOT;

/**
 * 基于zookeeper的分布式锁实现
 * 说明：当与zookeeper的会话断开时，它所持有的锁是否会自动释放？ 自动释放才合理！
 *
 * @author happyyangyuan
 */
public class ZkDistributedLock {
    /**
     * timed out waiting for the lock
     */
    public static final int TIME_OUT_INNER_ID = -1;

    private static final Map<Integer, InterProcessMutex> map = new ConcurrentHashMap<>();
    private static final AtomicInteger innerIdGenerator = new AtomicInteger(0);

    /**
     * lock the named lock
     * 注意：加锁和解锁必须配对地调用！Each call to acquire that returns true must be balanced by a call to release()
     *
     * @return the inner id of the lock or -1 if timed out waiting for the lock
     * @throws RuntimeException any unknown runtime exception.
     */
    public static int lock(String name, long timeoutInMilli) {
        if (name.contains("/")) {
            throw new IllegalArgumentException("锁的名称不允许包含'/'，yourLockName= " + name);
        }
        LOG.debug("由于zk单个znode下如果存在过多children会出现zk客户端无法ls列表，和无法rmr的问题，因此加入日期子路径");
        //todo 缺点1、一天的锁数量日后太大时也会有children过多问题
        //todo 缺点2、跨天锁失效
        //todo 以上解决方案：使用一致性哈希将锁分配到固定子路径内去
        //todo 优点、临时解决了我们维护zk困难的问题
        InterProcessMutex lock = new InterProcessMutex(ZkConnection.client, LOCK_ROOT + "/" + DateUtil.getDateStr() + "/" + name);
        int innerId = innerIdGenerator.getAndIncrement();
        try {
            if (!lock.acquire(timeoutInMilli, TimeUnit.MILLISECONDS)) {
                LOG.info(String.format("获取锁%s超时,超时时间%sms", name, timeoutInMilli));
                return TIME_OUT_INNER_ID;
            } else {
                map.put(innerId, lock);
                return innerId;
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解锁
     * 注意：1、加锁和解锁必须配对地调用！
     * 2、解锁线程必须与加锁线程是同一个,即不支持那种异步回调解锁！
     *
     * @return true if unlock successfully otherwise false. We don't care how the unlock fails.
     */
    public static boolean unlock(int innerId) {
        InterProcessMutex mutex = map.remove(innerId);
        if (mutex != null) {
            if (mutex.isAcquiredInThisProcess()) {
                try {
                    mutex.release();
                    LOG.debug("解锁成功...");
                    return true;
                } catch (Exception e) {
                    LOG.error("解锁失败！", e);
                    return false;
                }
            } else {
                LOG.error(new LockNotOwnedByCurrentThread());
                return false;
            }
        } else {
            //这里暂时不抛出异常，只打印日志
            LOG.error(new RuntimeException("API误用,根本不存在锁，你解什么锁?"));
            return false;
        }
    }

    public static class LockNotOwnedByCurrentThread extends RuntimeException {

        @Override
        public String getLocalizedMessage() {
            return "解锁线程必须与加锁线程是同一个,不支持那种异步回调解锁！";
        }
    }

}
