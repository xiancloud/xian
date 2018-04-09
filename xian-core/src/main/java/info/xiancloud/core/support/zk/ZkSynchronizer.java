package info.xiancloud.core.support.zk;

import io.reactivex.Single;

import java.util.concurrent.Callable;

/**
 * 分布式并发安全的同步任务执行器
 *
 * @author happyyangyuan
 * @deprecated zookeeper distributed lock is proved to have poor performance because zookeeper is not good at being frequently written.
 */
public class ZkSynchronizer {

    /**
     * 分布式并发安全地执行指定的任务，阻塞直到任务执行完毕才返回，阻塞直到任务执行完毕才释放锁。
     *
     * @return true if lock has bean gained, and the job has been run, false if timed out waiting for the lock.
     * Or return the exception thrown by the runnable.
     */
    public static Single<Boolean> run(String lockId, Runnable blockingRunnable, long timeoutInMilli) {
        return
                call(lockId, () -> {
                    blockingRunnable.run();
                    return null;
                }, timeoutInMilli)
                        .map(objectSynchronizerResult -> !objectSynchronizerResult.isTimedOut());
    }

    /**
     * 试探lockId是否被占用，如果未被占用那么执行指定任务，否则直接退出不执行任何动作。
     * 注意本方法不做业务级别的异常处理，请在业务层自己捕获和处理异常
     *
     * @param lockId           the business id.
     * @param blockingRunnable the job to run.
     * @return completed if lock has bean gained and the job has been run, otherwise return the exception thrown by the runnable.
     */
    public static Single<Boolean> runIfNotLocked(String lockId, Runnable blockingRunnable) {
        return run(lockId, blockingRunnable, 0);
    }

    /**
     * 分布式并发安全地执行指定的任务，并返回执行结果;
     * 阻塞直到任务执行完毕才返回，阻塞直到任务执行完毕才释放锁。
     *
     * @return the callable result or the exception thrown by the callable
     */
    public static <T> Single<SynchronizerResult<T>> call(String lockId, Callable<T> blockingCallable, long timeoutInMilli) {
        return DistZkLocker
                .lock(lockId, timeoutInMilli)
                .flatMap(lockInnerId -> {
                    SynchronizerResult<T> synchronizerResult = new SynchronizerResult<>();
                    if (DistZkLocker.TIME_OUT_INNER_ID != lockInnerId) {
                        synchronizerResult.setTimedOut(false);
                        try {
                            T result = blockingCallable.call();
                            synchronizerResult.setCallableResult(result);
                            return Single.just(synchronizerResult);
                        } finally {
                            DistZkLocker.unlock(lockInnerId).subscribe();
                        }
                    } else {
                        synchronizerResult.setTimedOut(true);
                        return Single.just(synchronizerResult);
                    }
                });
    }

    public static class SynchronizerResult<T> {
        private T callableResult;//can be null
        private boolean timedOut;

        public T getCallableResult() {
            return callableResult;
        }

        public void setCallableResult(T callableResult) {
            this.callableResult = callableResult;
        }

        public boolean isTimedOut() {
            return timedOut;
        }

        public void setTimedOut(boolean timedOut) {
            this.timedOut = timedOut;
        }
    }


}
