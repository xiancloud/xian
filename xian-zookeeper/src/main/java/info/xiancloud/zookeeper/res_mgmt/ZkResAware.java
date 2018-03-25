package info.xiancloud.zookeeper.res_mgmt;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import info.xiancloud.core.distribution.res.IResAware;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.Reflection;
import info.xiancloud.zookeeper.ZkConnection;
import info.xiancloud.zookeeper.ZkPathManager;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 资源变动受体；
 * todo 1、有时间时改造为properties缓存，而不是直接使用pathChildrenCache，原因：对于{@link #getAll(String)}需要遍历然后byte[]转字符串，对于{@link #get(String, String)}需要byte[]转字符串
 * todo 2、如果可以，增加一个扩展了的pathChildrenCache类，实现同时缓存children以及父节点data的功能，解决{@link #getVersion(String)}实时查询zk的问题
 *
 * @author happyyangyuan
 */
public class ZkResAware implements IResAware {

    private LoadingCache<String, PathChildrenCache> resCaches = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .removalListener((RemovalListener<String, PathChildrenCache>) notification -> {
                try {
                    notification.getValue().close();
                } catch (IOException e) {
                    LOG.error(e);
                }
            })
            .build(new CacheLoader<String, PathChildrenCache>() {
                @Override
                public PathChildrenCache load(String pluginName) throws Exception {
                    PathChildrenCache cache = new PathChildrenCache(ZkConnection.client, fullPath(pluginName), true);
                    cache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
                    return cache;
                }
            });

    @Override
    public String get(String pluginName, String key) {
        if (!ZkConnection.isConnected()) {
            return null;
        }
        ChildData childData = resCaches.getUnchecked(pluginName).getCurrentData(fullPath(pluginName + "/" + key));
        if (childData == null) return null;
        return new String(childData.getData());
    }

    @Override
    public Properties getAll(String pluginName) {
        Properties properties = new Properties();
        if (!ZkConnection.isConnected()) {
            return properties;
        }
        for (ChildData childData : resCaches.getUnchecked(pluginName).getCurrentData()) {
            properties.put(childData.getPath().substring(childData.getPath().lastIndexOf("/") + 1), new String(childData.getData()));
        }
        return properties;
    }

    @Override
    public String getVersion(String pluginName) {
        if (!ZkConnection.isConnected()) {
            return null;
        }
        LOG.debug("注意：查询版本号操作是实时查询zk，没有做缓存，不允许高频操作：" + pluginName);
        try {
            byte[] versionData = ZkConnection.client.getData().forPath(fullPath(pluginName));
            LOG.debug("if path exits but no data found, then empty byte array is returned. Thus here we need a empty array checking.");
            ResPluginDataBean resPluginDataBean = Reflection.toType(new String(versionData), ResPluginDataBean.class);
            if (resPluginDataBean != null)
                return resPluginDataBean.getVersion();
            return null;
        } catch (KeeperException.NoNodeException notExists) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String fullPath(String subPath) {
        return ZkPathManager.getResRootPath() + "/" + subPath;
    }

    @Override
    public void destroy() {
        resCaches.invalidateAll();//删除缓存会自动触发销毁动作
    }
}
