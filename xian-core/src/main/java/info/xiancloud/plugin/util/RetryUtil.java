package info.xiancloud.plugin.util;

import info.xiancloud.plugin.thread_pool.ThreadPoolManager;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author happyyangyuan
 */
public class RetryUtil {

    private static List<RetryTask> retryTasks = new CopyOnWriteArrayList<RetryTask>();
    private static long daemonPeriod = 1000L;

    static {
        //为定时任务启动一个后台轮询线程
        ThreadPoolManager.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // LOG.info("---------------任务开始执行了,任务数: " + retryTasks.size());
                for (RetryTask retryTask : retryTasks) {
                    Long currentTime = System.currentTimeMillis() / 1000L;
                    boolean condition = false;
                    int waitTime = 0;
                    if (retryTask.count == 0) { //首次执行
                        condition = true;
                    } else {
                        //需要等待的时间
                        waitTime = (retryTask.count) * retryTask.intervalTime;
                        //LOG.info("-----------waitTime: " + waitTime);
                        if (retryTask.maxTry > 0 && (currentTime - retryTask.lastTime) >= waitTime) {
                            condition = true;
                        }
                    }
                    // LOG.info("----------条件: " + condition);
                    if (condition) { //符合执行条件
                        retryTask.lastTime = currentTime;
                        retryTask.maxTry--;
                        retryTask.count++;
                        ThreadPoolManager.execute(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (retryTask) {  //如果使用复制的话，内存比较大，使用单个分离锁，防止并发执行问题
                                    //判断是否已经被移除了
                                    if (retryTasks.contains(retryTask)) {
                                        try {
                                            //LOG.info(String.format("----------第%s次执行,剩余次数:%s", retryTask.count, retryTask.maxTry));
                                            Object r = retryTask.callable.call();
                                            retryTask.getRetryFuture().setDone(r);
                                            retryTasks.remove(retryTask);
                                        } catch (Exception e) {
                                            LOG.error(e);
                                            if (retryTask.maxTry == 0) {
                                                retryTask.getRetryFuture().setDone();
                                                retryTasks.remove(retryTask);
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }, daemonPeriod);
    }

    /**
     * 重试直到无异常抛出或者超过最大重试次数
     */
    public static <Return> Return retryUntilNoException(Callable<Return> callable, int maxTry) throws Throwable {
        String errMsg = "连续重试失败" + maxTry + "次。";
        Throwable cause = null;
        while (maxTry > 0) {
            try {
                return callable.call();
            } catch (Throwable throwable) {
                cause = throwable;
                maxTry--;
            }
        }
        LOG.error(new Throwable(errMsg, cause));
        throw cause;
    }

    /**
     * 重试直到无异常抛出或者超过最大重试次数；
     * 原样抛出原异常
     */
    public static <Return> Return retryUntilNoException(Callable<Return> callable, int maxTry, Class<? extends Throwable> exceptionClass) throws Throwable {
        String errMsg = "连续重试失败" + maxTry + "次。";
        Throwable t = null;
        while (maxTry > 0) {
            try {
                return callable.call();
            } catch (Throwable throwable) {
                if (exceptionClass.isAssignableFrom(throwable.getClass())) {
                    //只对指定的异常重试，其他异常直接抛出去
                    maxTry--;
                    t = throwable;
                } else {
                    throw throwable;
                }
            }
        }
        LOG.error(new RuntimeException(errMsg, t));
        //这里是对重试N次后依然抛出指定异常的情况，原样抛出
        throw t;
    }


    /**
     * @param callable
     * @param intervalTime 递增执行的间隔时间
     * @param maxTry
     * @param delayTime    延时执行
     * @param <R>
     * @return
     * @throws Exception
     */
    public static <R> R retry(Callable<R> callable, int intervalTime, int maxTry, int delayTime) throws Exception {

        if (delayTime > 0) {
            LOG.info(String.format("-----延时%s秒执行", delayTime));
            Thread.sleep(delayTime);
        }
        return retry(callable, intervalTime, maxTry);
    }

    public static <R> R retry(Callable<R> callable, int intervalTime, int maxTry) throws Exception {
        if (callable == null)
            throw new NullPointerException("callable 不能为空");
        int count = 0;
        Exception cause = null;
        while (maxTry > 0) {
            count++;
            LOG.info(String.format("-----第%s次执行", count));
            try {
                if (count == 1) {
                    return callable.call();
                } else {
                    int waitTime = (count - 1) * intervalTime;
                    LOG.info(String.format("-----休息%s秒", waitTime));
                    Thread.sleep(waitTime);
                    return callable.call();
                }
            } catch (Exception e) {
                maxTry--;
                cause = e;
            }
        }
        throw cause;
    }

    public static <R> Future<R> retryTask(Callable<R> callable, int intervalTime, int maxTry) {

        RetryTask<R> retryTask = new RetryTask<R>(callable, maxTry, intervalTime);
        retryTasks.add(retryTask);
        return retryTask.getRetryFuture();
    }

    /**
     * 暂时没用上
     *
     * @param <R>
     */
    static class RetryTask<R> {
        public Callable<R> callable;
        public int maxTry; //最大重试次数
        public int intervalTime; //重试间隔时间
        public int delayTime;//延时执行时间
        public long lastTime; //最后重试时间
        public int count;
        RetryFuture<R> retryFuture;

        public RetryTask(Callable callable, int maxTry, int intervalTime) {
            this(callable, maxTry, intervalTime, 0);
        }

        public RetryTask(Callable callable, int maxTry, int intervalTime, int delayTime) {
            if (callable == null)
                throw new NullPointerException("callable can not be null");
            this.callable = callable;
            this.maxTry = maxTry;
            this.intervalTime = intervalTime;
            this.delayTime = delayTime;
            this.lastTime = System.currentTimeMillis() / 1000L;
            retryFuture = new RetryFuture<R>();
        }

        public RetryFuture<R> getRetryFuture() {
            return retryFuture;
        }
    }

    static class RetryFuture<R> implements Future<R> {
        private final Object lock = new Object();
        private boolean done;
        private R r;

        /**
         * @return
         * @throws InterruptedException
         * @throws ExecutionException
         */
        @Override
        public R get() throws InterruptedException, ExecutionException {
            synchronized (lock) {
                while (!isDone()) {
                    lock.wait();
                }
            }
            return r;
        }

        @Override
        public R get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            if (timeout <= 0) {
                throw new IllegalArgumentException("超时时间必须为正数");
            }
            long timeoutInMilliseconds = unit.toMillis(timeout);
            synchronized (lock) {
                while (!isDone()) {
                    try {
                        lock.wait(timeoutInMilliseconds);
                    } catch (InterruptedException e) {
                        throw e;
                    }
                    if (!isDone()) {
                        throw new TimeoutException(String.format("任务超时:%s", timeout));
                    }
                }
            }
            return r;
        }

        public void setDone() {
            setDone(null);
        }

        public void setDone(R r) {
            synchronized (lock) {
                done = true;
                this.r = r;
                lock.notifyAll();
            }
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        public boolean isDone() {
            return done;
        }
    }

}
