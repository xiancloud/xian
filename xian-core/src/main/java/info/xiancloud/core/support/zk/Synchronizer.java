package info.xiancloud.core.support.zk;

import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * 分布式并发安全的同步任务执行器
 *
 * @author happyyangyuan
 */
public class Synchronizer {

    /**
     * 分布式并发安全地执行指定的任务，阻塞直到任务执行完毕才返回，阻塞直到任务执行完毕才释放锁。
     *
     * @param consumer the result of lock and run this job, a timeout code will be passed to the consumer if waiting lock timeout.
     */
    public static void run(String lockId, Runnable blocingRunnable, long timeoutInMilli, Consumer<UnitResponse> consumer) {
        call(lockId, () -> {
            blocingRunnable.run();
            return null;
        }, timeoutInMilli, consumer);
    }

    /**
     * 试探lockId是否被占用，如果未被占用那么执行指定任务，否则直接退出不执行任何动作。
     * 注意本方法不做业务级别的异常处理，请在业务层自己捕获和处理异常
     *
     * @param lockId   锁id
     * @param job      要执行的任务
     * @param consumer unit response of boolean is passed to this consumer.
     */
    public static void runIfNotLocked(String lockId, Runnable job, Consumer<UnitResponse> consumer) {
        run(lockId, job, 0, consumer);
    }

    /**
     * 分布式并发安全地执行指定的任务，并返回执行结果;
     * 阻塞直到任务执行完毕才返回，阻塞直到任务执行完毕才释放锁。
     */
    public static <T> void call(String lockId, Callable<T> blockingCallable, long timeoutInMilli, Consumer<UnitResponse> consumer) {
        DistLocker.lock(lockId, timeoutInMilli, unitResponse -> {
            if (unitResponse.succeeded()) {
                try {
                    T result = blockingCallable.call();
                    consumer.accept(UnitResponse.createSuccess(result));
                } catch (Exception e) {
                    LOG.error(e);
                    consumer.accept(UnitResponse.createException(e));
                } finally {
                    DistLocker.unlock(unitResponse.dataToType(Integer.class), unitResponse1 -> {
                    });
                }
            } else
                consumer.accept(unitResponse);
        });
    }


}
