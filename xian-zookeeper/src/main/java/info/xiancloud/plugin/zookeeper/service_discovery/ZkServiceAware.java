package info.xiancloud.plugin.zookeeper.service_discovery;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.distribution.LocalNodeManager;
import info.xiancloud.plugin.distribution.MessageType;
import info.xiancloud.plugin.init.IStartService;
import info.xiancloud.plugin.message.IdManager;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.zookeeper.ZkConnection;
import info.xiancloud.plugin.zookeeper.ZkPathManager;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;

/**
 * 远程服务自动感知，其本质就是在本地维护一份远程服务缓存，并监听远程服务的变动
 *
 * @author happyyangyuan
 * @deprecated 服务发现已经重构，本代码用于归档暂时不删除
 */
public class ZkServiceAware /*implements IStartService*/ {

    private TreeCache allNodesCache;
    private volatile static ZkServiceAware serviceAware;
    private static final Object lock = new Object();

    /**
     * 请勿动，本构造器提供给反射{@link IStartService 启动项实例化}使用的
     */
    public ZkServiceAware() {
    }

    private static void start() {
        synchronized (lock) {
            if (serviceAware != null) {
                return;
            }
            serviceAware = new ZkServiceAware();
            LOG.info("开始启动zk服务发现,对serviceAware加锁，保证service启停并发安全");
            serviceAware.allNodesCache = TreeCache.newBuilder(ZkConnection.client, ZkPathManager.getNodeBasePath()).build();
            serviceAware.allNodesCache.getListenable().addListener((client, event) -> {
                LOG.debug(String.format("监听到节点%s的%s事件",
                        event.getData() != null ? event.getData().getPath() : null,
                        event.getType()));
                JSONObject payload;
                switch (event.getType()) {
                    case NODE_ADDED:
                    case NODE_UPDATED:
                        if (!ZkPathManager.isServiceNode(event.getData())) {
                            LOG.info("排除掉那些树干节点等辅助节点:" + event.getData().getPath());
                            break;
                        }
                        JSONObject nodeData = getNodeData(event);
                        payload = getPayload(nodeData);
                        String clientId = payload.getString("$LOCAL_NODE_ID");
                        if (IdManager.isLocalNodeId(clientId)) {
                            LOG.info("本地服务不适用zk远程服务注册机制，这里跳过把本地服务注册到本地的过程");
                            break;
                        }
                        if (!isEnabled(nodeData)) {
                            LOG.info(new JSONObject() {{
                                put("type", "nodeDisabled");
                                put("disabledNodeId", client);
                                put("description", "节点被设置为不可用");
                            }});
                            payload.put("$msgType", MessageType.offline);
                            break;
                        }
                        LOG.info("zk监听到远程节点注册:   " + clientId);
                        payload.put("$msgType", MessageType.ping);
                        break;
                    case CONNECTION_LOST:
                        LOG.debug("如果自己与zookeeper的连接断开，那么请不要使用本节点对外的任何网络请求了");
                        LOG.error("与zookeeper的连接异常断开", new Throwable());
                        break;
                    case NODE_REMOVED:
                        if (ZkPathManager.isServiceNode(event.getData())) {
                            payload = getPayload(getNodeData(event));
                            if (LocalNodeManager.LOCAL_NODE_ID.equals(payload.get("$nodeId")))
                                break;//自己掉线则忽略掉
                            LOG.info("节点离线/掉线:" + payload.get("$nodeId"));
                            payload.put("$msgType", MessageType.offline);
                            /*ClientsManager.updateClients(payload);*/
                        } else {
                            LOG.debug("排除掉那些树干节点等辅助节点:" + event.getData().getPath());
                        }
                        break;
                }
            });
            try {
                serviceAware.allNodesCache.start();
            } catch (Exception e) {
                LOG.error(e);
            }
            LOG.info("zk服务发现启动完毕......");
        }
    }

    public static void stop() {
        synchronized (lock) {
            if (serviceAware != null)
                serviceAware.allNodesCache.close();
            serviceAware = null;
        }
    }

    private static JSONObject getNodeData(TreeCacheEvent event) {
        return JSON.parseObject(new String(event.getData().getData()));
    }

    private static JSONObject getPayload(JSONObject nodeData) {
        return nodeData.getJSONObject("payload");
    }

    private static boolean isEnabled(JSONObject nodeData) {
        return nodeData.getBooleanValue("enabled");
    }

    public boolean startup() {
        ZkServiceAware.start();
        return true;
    }

    public float startupOrdinal() {
        return -998;
    }

}
