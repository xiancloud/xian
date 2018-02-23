package info.xiancloud.plugin.support.zk;

import info.xiancloud.plugin.util.LOG;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

/**
 * 分布式并发安全的同步任务执行器
 *
 * @author happyyangyuan
 */
public class Synchronizer {

    /**
     * 分布式并发安全地执行指定的任务，阻塞直到任务执行完毕才返回，阻塞直到任务执行完毕才释放锁。
     *
     * @throws TimeoutException 如果获取锁超时那么抛出超时异常，任务不会被执行。
     */
    public static void run(String lockId, Runnable job, long timeoutInMilli) throws TimeoutException {
        call(lockId, () -> {
            job.run();
            return null;
        }, timeoutInMilli);
    }

    /**
     * 试探lockId是否被占用，如果未被占用那么执行指定任务，否则直接退出不执行任何动作。
     * 注意本方法不做业务级别的异常处理，请在业务层自己捕获和处理异常
     *
     * @param lockId 锁id
     * @param job    要执行的任务
     * @return 如果任务被执行，那么返回true,否则返回false
     */
    public static boolean runIfNotLocked(String lockId, Runnable job) {
        try {
            run(lockId, job, 0);
        } catch (TimeoutException e) {
            LOG.info("锁已经被占用，本次任务不会执行:" + lockId);
            return false;
        }
        return true;
    }

    /**
     * 分布式并发安全地执行指定的任务，并返回执行结果;
     * 阻塞直到任务执行完毕才返回，阻塞直到任务执行完毕才释放锁。
     *
     * @return T 执行结果
     * @throws TimeoutException 获取锁超时则会抛出超时异常，任务不会被执行。
     */
    public static <T> T call(String lockId, Callable<T> callable, long timeoutInMilli) throws TimeoutException {
        DistLocker locker = DistLocker.lock(lockId, timeoutInMilli);
        try {
            return callable.call();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }


}
