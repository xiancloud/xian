package info.xiancloud.plugin.thread_pool;

import info.xiancloud.plugin.Group;

/**
 * @author happyyangyuan
 */
public class ThreadPoolGroup implements Group {
    @Override
    public String getName() {
        return "threadPool";
    }

    @Override
    public String getDescription() {
        return "thread pool unit group.";
    }

}