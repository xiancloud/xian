package info.xiancloud.zookeeper.res_mgmt;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.distribution.res.IResAware;
import info.xiancloud.core.distribution.res.ResInit;
import info.xiancloud.core.message.id.NodeIdBean;
import info.xiancloud.core.util.LOG;
import info.xiancloud.zookeeper.ZkConnection;
import info.xiancloud.zookeeper.ZkPathManager;
import info.xiancloud.zookeeper.lock.ZkDistributedLock;
import org.apache.zookeeper.KeeperException;

import java.util.Objects;
import java.util.Properties;

/**
 * 向zk注册配置启动项的zk实现
 *
 * @author happyyangyuan
 */
public class ZkResInit extends ResInit {
    @Override
    protected void register(String plugin, String version, Properties properties) {
        String pluginFullPath = getFullPath(plugin);
        String zkLockKey = zkLockKey(pluginFullPath);
        int innerLockId = ZkDistributedLock.lock(zkLockKey, 0);
        if (innerLockId != -1) {
            LOG.debug("相同的path可能同时被N个进程写到zk内，因此需要做分布式级别的并行控制");
            try {
                //写版本号到节点的data内,方便后续做版本对比
                byte[] pluginMetaDataBytes = new JSONObject() {{
                    put("version", version);
                }}.toJSONString().getBytes();
                try {
                    ZkConnection.client.setData()/*.withVersion(version.hashCode())*/.forPath(pluginFullPath, pluginMetaDataBytes);
                } catch (KeeperException.NoNodeException noNode) {
                    ZkConnection.client.create().creatingParentsIfNeeded().forPath(pluginFullPath, pluginMetaDataBytes);
                }
                for (String key : properties.stringPropertyNames()) {
                    String configSingleKeyPath = pluginFullPath + "/" + key;
                    byte[] valueBytes = properties.getProperty(key).getBytes();
                    try {
                        ZkConnection.client.setData().forPath(configSingleKeyPath, valueBytes);
                    } catch (KeeperException.NoNodeException e) {
                        LOG.info("不存在zk节点:" + pluginFullPath + "/" + key + ",创建一个");
                        ZkConnection.client.create().forPath(configSingleKeyPath, valueBytes);
                    } catch (Throwable e) {
                        LOG.error(String.format("同步本地配置到注册中心时出现异常：key=%s，value=%s", key, properties.getProperty(key)), e);
                    }
                }
                LOG.info(String.format("plugin %s ,   version  %s  ,   configurations has been written to zookeeper.", plugin, version));
            } catch (Throwable innerError) {
                LOG.error(String.format("plugin %s ,   version  %s  ,   failed to write configurations to zookeeper.", plugin, version), innerError);
            } finally {
                if (!ZkDistributedLock.unlock(innerLockId)) {
                    LOG.warn("lock " + zkLockKey + " unlock failed.");
                }
            }
        } else {
            LOG.info(String.format("plugin %s ,   version  %s  ,   configurations has already been written to zookeeper by other nodes. Nothing is done here.", plugin, version));
        }
    }

    @Override
    protected boolean isNewVersion(String plugin, String version) {
        return !Objects.equals(IResAware.singleton.getVersion(plugin), version);
    }

    private String zkLockKey(String pluginResFullPath) {
        return getClass().getName() + NodeIdBean.splitter + pluginResFullPath.replace("/", "|");
    }


    private String getFullPath(String subPath) {
        if (subPath.contains("/")) {
            throw new IllegalArgumentException("'/' is not allowed in node sub path: " + subPath);
        }
        return ZkPathManager.getResRootPath() + "/" + subPath;
    }

}
