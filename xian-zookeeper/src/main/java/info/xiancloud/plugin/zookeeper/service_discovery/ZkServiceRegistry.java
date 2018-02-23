package info.xiancloud.plugin.zookeeper.service_discovery;

import com.alibaba.fastjson.JSON;
import info.xiancloud.plugin.distribution.Node;
import info.xiancloud.plugin.distribution.LocalNodeManager;
import info.xiancloud.plugin.distribution.NodeStatus;
import info.xiancloud.plugin.util.EnvUtil;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.zookeeper.ZkConnection;
import info.xiancloud.plugin.zookeeper.ZkPathManager;
import info.xiancloud.plugin.zookeeper.utils.FastJsonServiceInstanceSerializer;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;

import java.io.IOException;

/**
 * 向zookeeper注册本节点服务，注意本类不会自动注册本地服务，本类只是提供一个注册服务的入口，需要手动调用{@link #registerService(String, NodeStatus)}来注册本地服务到远程zk内
 *
 * @author happyyangyuan
 * @deprecated 服务注册已重构，已经将节点注册和unit注册分离为二
 */
public class ZkServiceRegistry {
    private volatile static ZkServiceRegistry registry;
    private static final Object lock = new Object();
    private ServiceDiscovery<NodeStatus> serviceDiscovery;

    private ZkServiceRegistry() {
    }

    /**
     * 启动服务注册，只有启动了服务注册，才可以调用{@link #registerService(String, NodeStatus)}
     */
    public static void start() {
        synchronized (lock) {
            if (registry != null) return;
            try {
                LOG.info("开始启动zk服务注册入口");
                registry = new ZkServiceRegistry();
                FastJsonServiceInstanceSerializer<NodeStatus> serializer = new FastJsonServiceInstanceSerializer<>();
                registry.serviceDiscovery = ServiceDiscoveryBuilder.builder(NodeStatus.class)
                        .client(ZkConnection.client)
                        .basePath(ZkPathManager.getNodeBasePath())
                        .serializer(serializer)
                        .build();
                registry.serviceDiscovery.start();
                LOG.info("zk服务注册入口启动完毕");
                registerService(EnvUtil.getApplication(), LocalNodeManager.singleton.getFullStatus());
            } catch (Exception e) {
                throw new RuntimeException("启动zkServiceRegistry失败", e);
            }
        }
    }

    //注销本节点的服务
    public static void stop() {
        synchronized (lock) {
            if (registry == null) return;
            try {
                registry.serviceDiscovery.close();
            } catch (IOException e) {
                LOG.error(e);
            } finally {
                registry = null;
            }
        }
    }

    //注册服务，如果注册服务未启动过，那么阻塞直到它启动为止
    private static void registerService(String application, NodeStatus payload) {
        try {
            /*while (ZkBootstrap.zkNeverStarted()) {
                Thread.sleep(1000);
                LOG.warn("zk注册服务尚未初始化，所以无法将当前服务发送到注册中心,请等待...");
            }
            if (ZkBootstrap.zkEverStartedButNowStoped()) {
                LOG.warn("zk客户端已断开连接，不再执行注册服务动作");
                return;
            }*/
            synchronized (lock) {
                LOG.info("注册服务,详情：  " + JSON.toJSON(payload));
                registry.serviceDiscovery.registerService(ServiceInstance.<NodeStatus>builder()
                        .id(payload.getNodeId())
                        .name(application)
                        .payload(payload)
                        .build()
                );
            }
        } catch (Throwable e) {
            LOG.error(e);
        } finally {
            LOG.info(String.format("%s的服务注册完毕", application));
        }
    }

    //注销服务，如果你知道你在做什么那么请调用我,o 目前已经没必要单独注销服务了，直接stop服务注册器即可
    public static void unregisterService(String serviceName, String clientId) {
        try {
            if (registry != null) {
                synchronized (lock) {
                    ServiceInstance<NodeStatus> instance = registry.serviceDiscovery.queryForInstance(serviceName, clientId);
                    if (instance != null) {
                        registry.serviceDiscovery.unregisterService(instance);
                    } else {
                        LOG.warn("你要注销的服务不存在或已经被注销了,服务id:" + clientId);
                    }
                }
            } else {
                LOG.error("服务注册机已经停止，说明本机所有服务已经全部注销，所以没必要再单独注销服务了。", new Throwable());
            }
        } catch (Throwable e) {
            LOG.error(e);
        }
    }

}
