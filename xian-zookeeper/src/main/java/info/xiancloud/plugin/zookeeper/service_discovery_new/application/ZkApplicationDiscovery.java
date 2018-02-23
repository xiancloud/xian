package info.xiancloud.plugin.zookeeper.service_discovery_new.application;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import info.xiancloud.plugin.distribution.LocalNodeManager;
import info.xiancloud.plugin.distribution.Node;
import info.xiancloud.plugin.distribution.NodeStatus;
import info.xiancloud.plugin.distribution.event.NodeOfflineEvent;
import info.xiancloud.plugin.distribution.event.NodeOnlineEvent;
import info.xiancloud.plugin.distribution.event.NodeUpdatedEvent;
import info.xiancloud.plugin.distribution.service_discovery.ApplicationDiscovery;
import info.xiancloud.plugin.distribution.service_discovery.ApplicationInstance;
import info.xiancloud.plugin.event.EventPublisher;
import info.xiancloud.plugin.message.id.NodeIdBean;
import info.xiancloud.plugin.util.EnvUtil;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.zookeeper.ZkConnection;
import info.xiancloud.plugin.zookeeper.ZkPathManager;
import info.xiancloud.plugin.zookeeper.service_discovery_new.ZkServiceInstanceAdaptor;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.details.FastjsonServiceDefinitionSerializer;
import org.apache.curator.x.discovery.details.InstanceProvider;
import org.apache.curator.x.discovery.details.ServiceCacheListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 单例模式
 *
 * @author happyyangyuan
 */
public class ZkApplicationDiscovery implements ApplicationDiscovery {
    private ServiceDiscovery<NodeStatus> serviceDiscovery;
    private LoadingCache<String, ServiceProvider<NodeStatus>> serviceProviders = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)//30分钟后没人使用就释放这个监听器
            .removalListener((RemovalListener<String, ServiceProvider<NodeStatus>>) notification -> {
                try {
                    //释放watcher
                    notification.getValue().close();
                } catch (IOException e) {
                    LOG.error(e);
                }
            })
            .build(new CacheLoader<String, ServiceProvider<NodeStatus>>() {
                @Override
                public ServiceProvider<NodeStatus> load(String key) throws Exception {
                    ServiceProvider<NodeStatus> serviceProvider = serviceDiscovery.serviceProviderBuilder()
                            // 如果我传入一个不存在的service名，会成功返回一个discovery对象，然后一直返回null的服务实例
                            .serviceName(key)
                            .build();
                    serviceProvider.start();
                    serviceProvider.serviceCache().addListener(new ServiceCacheListener<NodeStatus>() {
                        @Override
                        public void cacheChanged() {
                        }

                        @Override
                        public void cacheChanged(PathChildrenCacheEvent event, ServiceInstance<NodeStatus> instance) {
                            ApplicationInstance applicationInstance = ZkServiceInstanceAdaptor.applicationInstance(instance);
                            switch (event.getType()) {
                                case CHILD_ADDED:
                                    EventPublisher.publish(new NodeOnlineEvent().setInstance(applicationInstance));
                                    break;
                                case CHILD_REMOVED:
                                    EventPublisher.publish(new NodeOfflineEvent().setInstance(applicationInstance));
                                    break;
                                case CHILD_UPDATED:
                                    EventPublisher.publish(new NodeUpdatedEvent().setInstance(applicationInstance));
                                    break;
                                default:
                                    LOG.debug("忽略其他事件：" + event.getType());
                                    break;
                            }
                        }

                        @Override
                        public void stateChanged(CuratorFramework client, ConnectionState newState) {
                        }
                    });
                    return serviceProvider;
                }
            });
    private LoadingCache<String, InstanceProvider<NodeStatus>> nonCachedInstanceProviders = CacheBuilder.newBuilder()
            .build(new CacheLoader<String, InstanceProvider<NodeStatus>>() {
                @Override
                public InstanceProvider<NodeStatus> load(String name) throws Exception {
                    return new InstanceProvider<NodeStatus>() {
                        @Override
                        public List<ServiceInstance<NodeStatus>> getInstances() throws Exception {
                            return (List<ServiceInstance<NodeStatus>>) serviceDiscovery.queryForInstances(name);
                        }

                        @Override
                        public ServiceInstance<NodeStatus> getInstance(String nodeId) {
                            try {
                                return serviceDiscovery.queryForInstance(name, nodeId);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    };
                }
            });

    public void init() {
        serviceDiscovery = ServiceDiscoveryBuilder.builder(NodeStatus.class)
                .basePath(ZkPathManager.getNodeBasePath())
                .client(ZkConnection.client)
                /*.thisInstance()*/
                .serializer(new ZkApplicationInstanceSerializer())
                /*.serializer(new FastJsonServiceInstanceSerializer<>(GroupInstance<NodeStatus>.class)) 试验以失败告终*/
                .serializer(new FastjsonServiceDefinitionSerializer<>(NodeStatus.class))
                .build();
        try {
            serviceDiscovery.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void destroy() {
        try {
            serviceProviders.invalidateAll();//释放所有watcher
            nonCachedInstanceProviders.invalidateAll();//清空缓存
            serviceDiscovery.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void register() {
        try {
            serviceDiscovery.registerService(thisInstance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void unregister() {
        try {
            serviceDiscovery.unregisterService(thisInstance());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static ServiceInstance<NodeStatus> thisInstance() throws Exception {
        return ServiceInstance.<NodeStatus>builder()
                .address(EnvUtil.getLocalIp())
                .enabled(true)
                .id(LocalNodeManager.LOCAL_NODE_ID)
                .name(EnvUtil.getApplication())
                .port(Node.RPC_PORT)
                .payload(LocalNodeManager.singleton.getFullStatus())
                .build();
    }

    @Override
    public ApplicationInstance lb(String name) {
        try {
            ServiceInstance<NodeStatus> serviceInstance = serviceProviders.get(name).getInstance();
            if (serviceInstance == null) {
                serviceInstance = serviceProviders.get(name).getProviderStrategy()
                        .getInstance(nonCachedInstanceProviders.get(name));
            }
            return ZkServiceInstanceAdaptor.applicationInstance(serviceInstance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ApplicationInstance> all(String name) {
        List<ApplicationInstance> applicationInstanceList = new ArrayList<>();
        try {
            List<ServiceInstance<NodeStatus>> allInstances = serviceProviders.get(name).getAllInstances();
            if (allInstances.isEmpty()) {
                allInstances = nonCachedInstanceProviders.get(name).getInstances();
            }
            for (ServiceInstance<NodeStatus> serviceInstance : allInstances) {
                applicationInstanceList.add(ZkServiceInstanceAdaptor.applicationInstance(serviceInstance));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return applicationInstanceList;
    }

    @Override
    public ApplicationInstance firstInstance(String name) {
        try {
            Collection<ServiceInstance<NodeStatus>> zkInstances = serviceProviders.get(name).getAllInstances();
            if (zkInstances.isEmpty())
                zkInstances = nonCachedInstanceProviders.get(name).getInstances();
            if (zkInstances.isEmpty()) return null;
            return ZkServiceInstanceAdaptor.applicationInstance(zkInstances.iterator().next());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApplicationInstance instance(String nodeId) {
        String name = NodeIdBean.parse(nodeId).getApplication();
        ServiceInstance<NodeStatus> instance = serviceProviders.getUnchecked(name).getInstance(nodeId);
        if (instance == null)
            instance = nonCachedInstanceProviders.getUnchecked(name).getInstance(nodeId);
        return ZkServiceInstanceAdaptor.applicationInstance(instance);
    }

    @Override
    public List<String> queryForNames() {
        try {
            LOG.info(new JSONObject() {{
                put("type", "queryZkForNames");
                /*put("description", "这个查询动作不能过于频繁，否则会有性能问题");*/
            }});
            return serviceDiscovery.queryForNames();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public NodeStatus newestDefinition(String name) {
        return serviceProviders.getUnchecked(name).getNewestServiceDefinition();
    }
}
