package info.xiancloud.zookeeper.cache;

import info.xiancloud.core.util.LOG;
import info.xiancloud.zookeeper.ZkConnection;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;

/**
 * @author happyyangyuan
 */
public class ZkTreeCache {
    private String root;
    private TreeCache treeCache;

    public ZkTreeCache(String root) {
        this.root = root;
        treeCache = TreeCache.newBuilder(ZkConnection.client, root)
                /*.setExecutor(ThreadPoolManager.getExecutor())*/
                .setCreateParentNodes(true)
                .build();
        try {
            treeCache.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        if (treeCache != null)
            treeCache.close();
    }

    /**
     * @param subPath 相对于{@link #root}的节点路径
     * @return 节点数据，如果节点不存在那么返回null
     */
    public String get(String subPath) {
        String fullPath = getFullPath(subPath);
        ChildData childData = treeCache.getCurrentData(fullPath);
        if (childData == null) {
            LOG.warn("节点" + fullPath + "不存在");
            return null;
        }
        String dataStr = new String(childData.getData());
        LOG.debug("歪歪日志: cache data=" + dataStr);
        return dataStr;
    }

    private String getFullPath(String subPath) {
        /*if (subPath.contains("/")) {
            throw new RuntimeException("'/' is not allowed in node sub path: " + subPath);
        }*/
        return root + "/" + subPath;
    }


}
