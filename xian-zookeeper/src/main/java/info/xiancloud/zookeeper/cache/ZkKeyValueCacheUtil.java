package info.xiancloud.zookeeper.cache;

import com.google.common.cache.*;
import info.xiancloud.core.init.shutdown.ShutdownHook;
import info.xiancloud.core.support.zk.DistLocker;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.Pair;
import info.xiancloud.zookeeper.ZkConnection;
import info.xiancloud.zookeeper.ZkPathManager;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.zookeeper.KeeperException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * nodeCache基础工具类.
 * 适用场景：
 * 1、简单的字符串key-value数据，读多写少的场景。
 * 2、允许写入延迟
 * todo 加入到xian-core内管理
 *
 * @author happyyangyuan
 */
public class ZkKeyValueCacheUtil implements ShutdownHook {

    /**
     * 注意：key为subPath，value为监听器和dataStr
     */
    private static final LoadingCache<String, Pair<NodeCache, String>> loadingCache = CacheBuilder.newBuilder()
            /*.maximumSize(10 * 1000)*/
            .expireAfterAccess(5, TimeUnit.MINUTES) //定时清除无人问津的数据
            .removalListener((RemovalListener<String, Pair<NodeCache, String>>) notification -> {
                try {
                    if (notification.getValue() != null && notification.getValue().fst != null) {
                        notification.getValue().fst.close();
                    }
                    if (notification.getCause() == RemovalCause.EXPLICIT) {
                        LOG.info("如果是被手动删除的，那么将远程的节点也删除");
                        ZkConnection.client.delete().deletingChildrenIfNeeded().forPath(notification.getKey());
                    }
                } catch (Throwable e) {
                    LOG.error(e);
                }
            })
            .build(new CacheLoader<String, Pair<NodeCache, String>>() {
                public Pair<NodeCache, String> load(String subPath) throws Exception {
                    NodeCache nodeCache = createNodeCache(subPath);
                    if (ZkConnection.client.checkExists().forPath(getFullPath(subPath)) == null) {
                        //创建节点是异步动作，因此有这个检查
                        return Pair.of(nodeCache, "");
                    }
                    return Pair.of(nodeCache, new String(ZkConnection.client.getData().forPath(getFullPath(subPath))));
                }
            });

    /**
     * 如果本地缓存未命中那么从远程找,是经典的"if cached, return; otherwise create, cache and return" pattern
     */
    public static String get(String subPath) {
        String dataStr = null;
        try {
            dataStr = loadingCache.get(subPath).snd;
        } catch (ExecutionException e) {
            LOG.error(e);
        }
        return dataStr;
    }

    /**
     * 设置分布式缓存的值，不建议高频次地对同一个key进行设置操作。
     * 只适合读频繁但写入少的场景
     *
     * @throws CacheLockedException 如果其他人正在写入该值，则禁止你写入.
     */
    public static void set(String subPath, String data) throws CacheLockedException {
        if (data == null) {
            data = "";
        }
        String fullPath = getFullPath(subPath);
        DistLocker locker;//遇到锁则直接超时不执行
        try {
            locker = DistLocker.lock(ZkKeyValueCacheUtil.class.getName() + "-lock:" + fullPath.replace("/", "_"), 0);
        } catch (TimeoutException rejected) {
            throw new CacheLockedException(subPath);
        }
        try {
            try {
                ZkConnection.client.setData().forPath(fullPath, data.getBytes());
            } catch (KeeperException.NoNodeException e) {
                ZkConnection.client.create().creatingParentContainersIfNeeded().forPath(fullPath, data.getBytes());
            }
            loadingCache.get(subPath).snd = data;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }

    /**
     * 同时删除本地与远程zk内的数据
     */
    public static void remove(String subPath) {
        loadingCache.invalidate(subPath);
    }

    private static String getFullPath(String subPath) {
        if (subPath.contains("/")) {
            throw new RuntimeException("'/' is not allowed in node sub path: " + subPath);
        }
        return ZkPathManager.CACHE_ROOT + "/" + subPath;
    }

    private static NodeCache createNodeCache(String subPath) {
        String fullPath = getFullPath(subPath);
        NodeCache nodeCache = new NodeCache(ZkConnection.client, fullPath);
        try {
            nodeCache.start();
        } catch (Exception e) {
            LOG.error(e);
        }
        nodeCache.getListenable().addListener(() -> {
            String newDataStr = new String(nodeCache.getCurrentData().getData());
            LOG.info("监听到zk缓存取值有变化：" + fullPath + "=" + newDataStr);
            loadingCache.get(subPath).snd = newDataStr;
        }/*, ThreadPoolManager.getExecutor()*/);
        return nodeCache;
    }

    @Override
    public boolean shutdown() {
        LOG.info("这里销毁时清空cache，同时会触发它操作zk的清理工作，可以避免zk存储泄露");
        loadingCache.invalidateAll();
        LOG.info("这里销毁时清空cache，同时会触发它操作zk的清理   完毕");
        return true;
    }

    /*@Override
    public float shutdownOrdinal() {
        LOG.debug("必须在zk连接注销之前完成");
        return new ZkConnection().shutdownOrdinal() - 1;
    }*/

    public static class CacheLockedException extends Exception {
        private String subPath;

        private CacheLockedException(String key) {
            subPath = key;
        }

        @Override
        public String getMessage() {
            return subPath + "正在被其他人操作中，暂时拒绝你操作.";
        }
    }


}