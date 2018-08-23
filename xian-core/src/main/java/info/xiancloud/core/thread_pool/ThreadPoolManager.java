package info.xiancloud.core.thread_pool;

import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.ProxyBuilder;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.core.util.thread.MsgIdHolder;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * xian统一的业务线程池,建议所有人都用本线程池内的线程来执行任务
 *
 * @author happyyangyuan
 */
public class ThreadPoolManager {

    private static final ThreadPoolExecutor EXECUTOR;

    static {
        EXECUTOR = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        EXECUTOR.setKeepAliveTime(5, TimeUnit.SECONDS);
        EXECUTOR.allowCoreThreadTimeOut(true);
    }

    private static final List<ExecutorService> EXECUTORS = new CopyOnWriteArrayList<ExecutorService>() {{
        add(EXECUTOR);
    }};
    private static final ScheduledExecutorService scheduledExecutorService = newScheduledExecutor(2);

    /**
     * 由业务层自行维护的线程池，这里只是对其进行监控而已；
     */
    private static final Set<ExecutorService> EXPLICIT_EXECUTORS = new CopyOnWriteArraySet<>();

    public static ThreadFactory threadFactory() {
        return EXECUTOR.getThreadFactory();
    }

    /**
     * 监控api
     */
    public static int poolSize() {
        int poolSize = 0;
        for (ExecutorService pool : EXECUTORS) {
            if (pool instanceof ThreadPoolExecutor) {
                poolSize += ((ThreadPoolExecutor) pool).getPoolSize();
            }
        }
        for (ExecutorService pool : EXPLICIT_EXECUTORS) {
            if (pool instanceof ThreadPoolExecutor) {
                poolSize += ((ThreadPoolExecutor) pool).getPoolSize();
            }
        }
        return poolSize;
    }

    /**
     * 监控api
     */
    public static int queueSize() {
        int queueSize = 0;
        for (ExecutorService pool : EXECUTORS) {
            if (pool instanceof ThreadPoolExecutor) {
                queueSize += ((ThreadPoolExecutor) pool).getQueue().size();
            }
        }
        for (ExecutorService pool : EXPLICIT_EXECUTORS) {
            if (pool instanceof ThreadPoolExecutor) {
                queueSize += ((ThreadPoolExecutor) pool).getQueue().size();
            }
        }
        return queueSize;
    }

    /**
     * 监控api
     */
    public static int activeCount() {
        int activeCount = 0;
        for (ExecutorService pool : EXECUTORS) {
            if (pool instanceof ThreadPoolExecutor) {
                activeCount += ((ThreadPoolExecutor) pool).getActiveCount();
            }
        }
        for (ExecutorService pool : EXPLICIT_EXECUTORS) {
            if (pool instanceof ThreadPoolExecutor) {
                activeCount += ((ThreadPoolExecutor) pool).getActiveCount();
            }
        }
        return activeCount;
    }

    /*
    public static void setCorePoolSize(int corePoolSize) {
        executor.setCorePoolSize(corePoolSize);
    }

    public static void setMaximumPoolSize(int maximumPoolSize) {
        executor.setMaximumPoolSize(maximumPoolSize);
    }*/

    /**
     * @throws RejectedExecutionException
     */
    public static Future<?> execute(Runnable runnable) throws RejectedExecutionException {
        return getValidExecutor().submit(wrapRunnable(runnable, MsgIdHolder.get()));
    }

    /**
     * 执行任务并返回定义了泛型的future对象
     *
     * @param callable callable
     * @param <T>      期待返回的结果类型
     * @return 任务执行的future对象
     */
    public static <T> Future<T> execute(Callable<T> callable) throws RejectedExecutionException {
        return getValidExecutor().submit(wrapCallable(callable, MsgIdHolder.get()));
    }

    /**
     * 获取有效的线程池
     */
    public static ExecutorService getValidExecutor() {
        ExecutorService executorToUse;
        if (EXECUTOR.isTerminating() || EXECUTOR.isShutdown() || EXECUTOR.isTerminated()) {
            executorToUse = Executors.newSingleThreadExecutor();
            LOG.info("由于线程池" + ThreadPoolManager.class.getSimpleName() + "不可用，因此使用临时线程提交此任务");
        } else {
            executorToUse = EXECUTOR;
        }
        return executorToUse;
    }

    /**
     * @throws RejectedExecutionException
     */
    public static Future<?> execute(Runnable runnable, String msgId) throws RejectedExecutionException {
        return getValidExecutor().submit(wrapRunnable(runnable, msgId));
    }

    /**
     * @param msgId 如果传入null，则会新分配一个$msgId；
     * @return 代理对象
     * @deprecated 弄AOP反而复杂化，请使用{@link #wrapRunnable(Runnable, String)}
     */
    private static Runnable getTrackerProxy(Runnable runnable, String msgId) {
        return new ProxyBuilder<Runnable>(runnable) {
            @Override
            public Object before(Method method, Object[] args) {
                if ("run".equals(method.getName())) {
                    if (!StringUtil.isEmpty(msgId)) {
                        MsgIdHolder.set(msgId);
                    } else {
                        LOG.debug("如果传入空的msgId那么新建一个给任务独享");
                        MsgIdHolder.init();
                    }
                }
                return null;
            }

            @Override
            public void after(Method method, Object[] args, Object methodReturn, Object beforeReturn) {
                if ("run".equals(method.getName())) {
                    if (methodReturn instanceof Throwable) {
                        LOG.error((Throwable) methodReturn);
                    }
                    MsgIdHolder.clear();
                }
            }
        }.getProxy();
    }

    /**
     * @param msgId 如果传入null，则会新分配一个$msgId；
     * @return 加入了一些底层处理的runnable
     */
    public static Runnable wrapRunnable(Runnable runnable, String msgId) {
        return () -> {
            if (!StringUtil.isEmpty(msgId)) {
                MsgIdHolder.set(msgId);
            } else {
                LOG.debug("如果传入空的msgId那么新建一个给任务独享");
                MsgIdHolder.init();
            }
            try {
                runnable.run();
            } catch (Throwable e) {
                LOG.error(e);
            } finally {
                MsgIdHolder.clear();
            }
        };
    }

    /**
     * @param msgId 如果传入null，则会新分配一个$msgId；
     * @return 加入了一些底层处理的runnable
     */
    public static <Return> Callable<Return> wrapCallable(final Callable<Return> callable, final String msgId) {
        return new Callable<Return>() {
            @Override
            public Return call() {
                if (!StringUtil.isEmpty(msgId)) {
                    MsgIdHolder.set(msgId);
                } else {
                    LOG.debug("如果传入空的msgId那么新建一个给任务独享");
                    MsgIdHolder.init();
                }
                try {
                    return callable.call();
                } catch (Throwable e) {
                    LOG.error(e);
                    return null;
                } finally {
                    MsgIdHolder.clear();
                }
            }
        };
    }

    public static Future<?> executeWithoutTrackingMsgId(Runnable runnable) {
        return getValidExecutor().submit(new ProxyBuilder<Runnable>(runnable) {
            @Override
            public Object before(Method method, Object[] args) throws OriginalResultReplacement {
                if (method.getName().equals("run")) {
                    MsgIdHolder.clear();
                }
                return null;
            }

            @Override
            public void after(Method method, Object[] args, Object methodReturn, Object beforeReturn)
                    throws OriginalResultReplacement {
                if (method.getName().equals("run")) {
                    MsgIdHolder.clear();
                }
            }
        }.getProxy());
    }

    /**
     * 轻量级的定时任务执行器。 任务之间不会并行执行，任何时刻都至多只会有一个任务在执行。
     * 如果下一个任务执行时间已经到了，但是前一个还没有执行完毕，那么下个任务等待直到前一个执行完，然后再马上开始.
     *
     * @param runnable      执行的任务
     * @param periodInMilli 任务启动固定间隔，单位毫秒
     */
    public static ScheduledFuture scheduleAtFixedRate(Runnable runnable, long periodInMilli) {
        /** 我们默认设定一个runnable生命周期与一个msgId一一对应 */
        Runnable proxied = wrapRunnable(runnable, null);
        return newSingleThreadScheduler().scheduleAtFixedRate(proxied, 0, periodInMilli, TimeUnit.MILLISECONDS);
    }

    /**
     * 前一个任务结束，等待固定时间，下一个任务开始执行
     *
     * @param runnable     你要提交的任务
     * @param delayInMilli 前一个任务结束后多久开始进行下一个任务，单位毫秒
     */
    public static ScheduledFuture scheduleWithFixedDelay(Runnable runnable, long delayInMilli) {
        /* 我们默认设定一个runnable生命周期与一个msgId一一对应 */
        Runnable proxy = wrapRunnable(runnable, null);
        return newSingleThreadScheduler().scheduleWithFixedDelay(proxy, 0, delayInMilli, TimeUnit.MILLISECONDS);
    }

    /**
     * Creates and executes a one-shot action that becomes enabled after the given delay.
     *
     * @param runnable     the runnable
     * @param delayInMilli the delay in milliseconds
     * @return the future
     */
    public static ScheduledFuture schedule(Runnable runnable, long delayInMilli) {
        Runnable proxy = wrapRunnable(runnable, null);
        return scheduledExecutorService.schedule(proxy, delayInMilli, TimeUnit.MILLISECONDS);
    }

    /**
     * create a new xian monitored scheduled thread pool
     *
     * @param corePoolSize the core pool size
     * @return the newly created thread pool
     */
    public static ScheduledExecutorService newScheduledExecutor(int corePoolSize) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(corePoolSize);
        EXECUTORS.add(scheduledExecutorService);
        return scheduledExecutorService;
    }

    // 新建一个executor并管理起来
    private static ScheduledExecutorService newSingleThreadScheduler() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        EXECUTORS.add(scheduledExecutorService);
        return scheduledExecutorService;
    }

    public static SingleThreadExecutorGroup newSingleTreadExecutorGroup(int size) {
        SingleThreadExecutorGroup group = new SingleThreadExecutorGroup(size);
        // 所有新创建的线程池都要管理起来的
        EXECUTORS.addAll(group.map.values());
        return group;
    }

    /**
     * @deprecated this is blocking execution
     */
    public static boolean execWithTimeout(final int timeoutInMilliseconds, final String name, final Callable task) {
        Future future = getValidExecutor().submit(task);
        try {
            return (boolean) future.get(timeoutInMilliseconds, TimeUnit.MILLISECONDS);
        } catch (TimeoutException ignoredE) {
            LOG.error("执行任务:'" + name + "'时发生超时：" + timeoutInMilliseconds + " ms");
            return false;
        } catch (Throwable e) {
            LOG.error("执行任务:'" + name + "'时发生错误", e);
            return false;
        }
    }

    /**
     * @deprecated this is blocking execution
     */
    public static boolean execWithTimeout(final int timeoutInMilliseconds, final String name, final Runnable task) {
        Future future = getValidExecutor().submit(task);
        try {
            future.get(timeoutInMilliseconds, TimeUnit.MILLISECONDS);
            return true;
        } catch (TimeoutException ignoredE) {
            LOG.error("任务超时：" + timeoutInMilliseconds + " ms，taskName = " + name);
            return false;
        } catch (Throwable e) {
            LOG.error("执行任务 '" + name + "' 时发生错误", e);
            return false;
        }
    }

    /**
     * 新建一个受管理的单线程池，你不需要注定关闭线程池，管理器会帮你关闭的
     * 注意：请复用，而不要频繁构造线程池
     */
    public static ExecutorService newSingleThreadPoolExecutor() {
        ExecutorService singleThreadPoolExecutor = Executors.newSingleThreadExecutor();
        EXECUTORS.add(singleThreadPoolExecutor);
        return singleThreadPoolExecutor;
    }

    /**
     * 监控相关；
     * 将独立线程池放入线程池监控器内；仅仅用于监控，但是不由本管理器来开启和shutdown；
     *
     * @param explicitThreadPool 由插件自行维护的外部线程池
     */
    public static void addExplicitThreadPool(ExecutorService explicitThreadPool) {
        EXPLICIT_EXECUTORS.add(explicitThreadPool);
    }

    // =======================================ShutdownHook======================

    /**
     * 默认销毁超时时间为10s，使用并行销毁
     *
     * @return 均成功销毁返回true，存在销毁超时的/失败的返回false
     */
    public static boolean destroy() {
        return destroy(10 * 1000);
    }

    /**
     * 销毁受管理的所有线程池，
     */
    public static boolean destroy(long timeoutInMillis) {
        final AtomicBoolean success = new AtomicBoolean(true);
        CountDownLatch latch = new CountDownLatch(EXECUTORS.size());
        for (ExecutorService exe : EXECUTORS) {
            new Thread(() -> {
                try {
                    exe.shutdown();
                    if (!exe.awaitTermination(timeoutInMillis, TimeUnit.MILLISECONDS)) {
                        success.set(false);
                        LOG.error(new TimeoutException("线程池销毁超时：" + exe));
                    }
                } catch (InterruptedException e) {
                    LOG.error(e);
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            LOG.error(e);
        }
        EXECUTORS.clear();
        EXPLICIT_EXECUTORS.clear();
        return success.get();
    }

}
