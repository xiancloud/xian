package info.xiancloud.core.support.zk;

import info.xiancloud.core.util.LOG;
import io.reactivex.Single;

import java.util.concurrent.Callable;

/**
 * 分布式并发安全的同步任务执行器
 *
 * @author happyyangyuan
 * @deprecated zookeeper distributed lock is proofed to be under poor performance due to zookeeper is not good at being frequently written.
 */
public class ZkSynchronizer {

    /**
     * 分布式并发安全地执行指定的任务，阻塞直到任务执行完毕才返回，阻塞直到任务执行完毕才释放锁。
     *
     * @return true if lock gained and the runable has bean run, false if waiting lock timeout, and runnable has not bean run.
     */
    public static Single<Boolean> run(String lockId, Runnable blockingRunnable, long timeoutInMilli) {
        return call(lockId, () -> {
            blockingRunnable.run();
            return null;
        }, timeoutInMilli);
    }

    /**
     * 试探lockId是否被占用，如果未被占用那么执行指定任务，否则直接退出不执行任何动作。
     * 注意本方法不做业务级别的异常处理，请在业务层自己捕获和处理异常
     *
     * @param lockId the business id.
     * @param job    the job to run.
     * @return true if lock has bean gained, and the job has bean run, otherwise false.
     */
    public static Single<Boolean> runIfNotLocked(String lockId, Runnable job) {
        return run(lockId, job, 0);
    }

    /**
     * 分布式并发安全地执行指定的任务，并返回执行结果;
     * 阻塞直到任务执行完毕才返回，阻塞直到任务执行完毕才释放锁。
     */
    public static <T> Single<T> call(String lockId, Callable<T> blockingCallable, long timeoutInMilli) {
        return DistZkLocker
                .lock(lockId, timeoutInMilli)
                .flatMap(lockInnerId -> {
                    try {
                        T result = blockingCallable.call();
                        DistZkLocker.unlock(lockInnerId);
                        return Single.just(result);
                    } catch (Exception e) {
                        LOG.error(e);
                        DistZkLocker.unlock(lockInnerId);
                        return Single.error(e);
                    }
                });
    }


}
