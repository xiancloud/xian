package info.xiancloud.core.thread_pool;

import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.core.util.thread.MsgIdHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 一组单线程执行器，原型类，非单例
 *
 * @author happyyangyuan
 */
public class SingleThreadExecutorGroup {

    Map<Integer, ThreadPoolExecutor> map = new HashMap<>();

    public int queueSize() {
        int size = 0;
        for (ThreadPoolExecutor singleThreadExecutor : map.values()) {
            //todo 这样循环迭代计算size之和的方式是否可以改为读取实时更新的缓存值？
            size += singleThreadExecutor.getQueue().size();
        }
        return size;
    }

    public int activeCount() {
        int size = 0;
        for (ThreadPoolExecutor singleThreadExecutor : map.values()) {
            //todo 这样循环迭代计算size之和的方式是否可以改为读取实时更新的缓存值？
            size += singleThreadExecutor.getActiveCount();
        }
        return size;
    }

    /**
     * 不要直接使用此构造器，请使用 {@link ThreadPoolManager#newSingleTreadExecutorGroup(int)}构造受管理的线程组
     */
    SingleThreadExecutorGroup(int threadCount) {
        for (int i = 0; i < threadCount; i++) {
            ThreadPoolExecutor singleThreadExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1, ThreadPoolManager.threadFactory());
            map.put(i, singleThreadExecutor);
        }
    }

    /**
     * sticky executor
     *
     * @param key      sticky key
     * @param runnable the task
     */
    public void execute(String key, Runnable runnable) {
        if (StringUtil.isEmpty(key)) {
            throw new IllegalArgumentException("入参key不允许为空！");
        }
        int mod = Math.abs(key.hashCode() % map.size());
        LOG.debug("_sequential   选取第" + mod + "根线程执行任务，threadKey=" + key);
        ThreadPoolExecutor executor = map.get(mod);
        executor.execute(ThreadPoolManager.wrapRunnable(runnable, MsgIdHolder.get()));
    }
}
