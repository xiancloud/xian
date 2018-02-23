package info.xiancloud.plugin.zookeeper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.util.EnvUtil;
import info.xiancloud.plugin.util.LOG;
import org.apache.curator.framework.recipes.cache.ChildData;

/**
 * zk本地path 统一在这里管理
 *
 * @author happyyangyuan
 */
public class ZkPathManager {
    /**
     * zk锁节点根路径，说明：锁只做环境隔离，不做多集群隔离，即集群间的锁都是互相可见的.
     */
    public static final String LOCK_ROOT = "/" + EnvUtil.getEnv() + "/locks";

    /**
     * key-value cache root path；说明：环境间隔离，集群间不隔离
     */
    public static final String CACHE_ROOT = "/" + EnvUtil.getEnv() + "/cache";

    /**
     * 资源管理/全局配置管理root path，说明：资源中心要做环境隔离，集群间隔离；
     * 格式为： /env/cluster/res/
     */
    public static String getResRootPath() {
        return "/" + EnvUtil.getEnv() + "/resources";
    }

    /**
     * xian所有的节点base path，可用于服务发现，支持多集群path。
     * 格式为： /env/cluster_name/nodes
     */
    public static String getNodeBasePath() {
        return "/" + EnvUtil.getEnv() + "/nodes";
    }

    /**
     * 当前节点所在的base path，可用于 leader 选举
     */
    public static String getMyNodeBasePath() {
        return getNodeBasePath() + "/" + EnvUtil.getApplication();
    }

    public static String getUnitBasePath() {
        return "/" + EnvUtil.getEnv() + "/units";
    }

    public static String getGroupBasePath() {
        return "/" + EnvUtil.getEnv() + "/groups";
    }

    //形如 /env/cluster_name/nodes/application/xxxx的节点才是服务节点，其他都不是
    public static boolean isServiceNode(ChildData zNode) {
        String path = zNode.getPath();
        LOG.debug("正在检查节点路径:   " + path);
        LOG.debug("服务注册路径：   " + getNodeBasePath());
        if (!path.startsWith(getNodeBasePath()) || path.equals(getNodeBasePath())) {
            return false;
        }
        String name = path.substring((getNodeBasePath() + "/").length());
        String[] names = name.split("/");
        if (names.length != 2) {
            return false;
        }
        /*if (!SystemEnum.isServiceNodeName(names[0])) {
            todo 由于去掉了systemEnum类，我们在watch到zk外部服务注册事件之前，根本不知道到底有哪些application
            return false;
        }*/
        String zNodeDataStr = new String(zNode.getData());
        JSONObject zNodeData;
        try {
            zNodeData = JSON.parseObject(zNodeDataStr);
        } catch (JSONException notJsonString) {
            LOG.debug(String.format("节点%s的data是%s，它不是我们要找的那个服务节点，排除掉！  这是我期待结果之一，不打印堆栈", path, zNodeDataStr));
            return false;
        }
        try {
            if (zNodeData.getJSONObject("payload") == null) {
                LOG.debug(String.format("节点=%s，内容=%s，其内缺少payload属性，它不是我们预期的服务节点！", path, zNodeDataStr));
                return false;
            }
        } catch (JSONException payloadIsNotJsonString) {
            LOG.info(String.format("节点%s的data=%s,其内的payload属性是不是json格式，肯定是有非预期的节点混入，这里排除之", path, zNodeDataStr));
            return false;
        }
        return true;
    }

    /**
     * 仅供内部使用
     *
     * @param path the zk tree full path
     */
    static void deletePath(String path) {
        try {
            ZkConnection.client.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
