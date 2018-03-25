package info.xiancloud.zookeeper.service_discovery_new.group;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import info.xiancloud.core.LocalUnitsManager;
import info.xiancloud.core.distribution.GroupProxy;
import info.xiancloud.core.distribution.service_discovery.GroupDiscovery;
import info.xiancloud.core.distribution.service_discovery.GroupInstance;
import info.xiancloud.core.distribution.service_discovery.GroupInstanceIdBean;
import info.xiancloud.core.util.LOG;
import info.xiancloud.zookeeper.ZkConnection;
import info.xiancloud.zookeeper.ZkPathManager;
import info.xiancloud.zookeeper.service_discovery_new.ZkServiceInstanceAdaptor;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.details.FastjsonServiceDefinitionSerializer;
import org.apache.curator.x.discovery.details.InstanceProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author happyyangyuan
 */
public class ZkGroupDiscovery implements GroupDiscovery {

    private ServiceDiscovery<GroupProxy> serviceDiscovery;
    private LoadingCache<String, ServiceProvider<GroupProxy>> serviceProviders = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)//30分钟后没人使用就释放这个监听器
            .removalListener((RemovalListener<String, ServiceProvider<GroupProxy>>) notification -> {
                try {
                    //释放watcher
                    notification.getValue().close();
                } catch (IOException e) {
                    LOG.error(e);
                }
            })
            .build(new CacheLoader<String, ServiceProvider<GroupProxy>>() {
                @Override
                public ServiceProvider<GroupProxy> load(String groupName) throws Exception {
                    ServiceProvider<GroupProxy> serviceProvider = serviceDiscovery.serviceProviderBuilder()
                            .serviceName(groupName)
                            .build();
                    serviceProvider.start();
                    return serviceProvider;
                }
            });
    private LoadingCache<String, InstanceProvider<GroupProxy>> nonCachedInstanceProviders = CacheBuilder.newBuilder()
            .build(new CacheLoader<String, InstanceProvider<GroupProxy>>() {
                @Override
                public InstanceProvider<GroupProxy> load(String groupName) throws Exception {
                    return new InstanceProvider<GroupProxy>() {
                        @Override
                        public List<ServiceInstance<GroupProxy>> getInstances() throws Exception {
                            return (List<ServiceInstance<GroupProxy>>) serviceDiscovery.queryForInstances(groupName);
                        }

                        @Override
                        public ServiceInstance<GroupProxy> getInstance(String id) {
                            try {
                                return serviceDiscovery.queryForInstance(groupName, id);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    };
                }
            });

    public void init() {
        serviceDiscovery = ServiceDiscoveryBuilder.builder(GroupProxy.class)
                .basePath(ZkPathManager.getGroupBasePath())
                .serializer(new ZkServiceInstanceSerializer())
                .serializer(new FastjsonServiceDefinitionSerializer<>(GroupProxy.class))
                .client(ZkConnection.client)
                .build();
        try {
            serviceDiscovery.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void destroy() {
        try {
            nonCachedInstanceProviders.invalidateAll();
            serviceProviders.invalidateAll();//释放所有watcher
            serviceDiscovery.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 向zk server注册本节点内所有units；
     * 注意，必须先init然后才可以执行本方法
     */
    public void selfRegister() {
        LocalUnitsManager.unitMap(unitMap -> {
            unitMap.forEach((groupName, unitsIgnored) -> {
                try {
                    ServiceInstance<GroupProxy> serviceInstance = ZkServiceInstanceAdaptor.thisCuratorServiceInstance(groupName);
                    serviceDiscovery.registerService(serviceInstance);
                } catch (Exception e) {
                    LOG.error(e);
                    //the registration should not be interrupted due to one error.
                }
            });
        });
    }

    /**
     * 注销本节点内所有unit实例
     */
    public void selfUnregister() {
        LocalUnitsManager.unitMap(unitMap -> {
            unitMap.forEach((groupName, unitListIgnored) -> {
                try {
                    ServiceInstance<GroupProxy> serviceInstance = ZkServiceInstanceAdaptor.thisCuratorServiceInstance(groupName);
                    serviceDiscovery.unregisterService(serviceInstance);
                } catch (Exception e) {
                    LOG.error(e);
                    //should not be interrupted due to one error.
                }
            });
        });
    }

    @Override
    public void register(GroupInstance groupInstance) throws Exception {
        serviceDiscovery.registerService(ZkServiceInstanceAdaptor.curatorServiceInstance(groupInstance));
    }

    @Override
    public void unregister(GroupInstance groupInstance) throws Exception {
        serviceDiscovery.unregisterService(ZkServiceInstanceAdaptor.curatorServiceInstance(groupInstance));
    }

    @Override
    public GroupInstance lb(String name) {
        try {
            ServiceInstance<GroupProxy> instance = serviceProviders.get(name).getInstance();
            if (instance == null)
                instance = serviceProviders.get(name).getProviderStrategy()
                        .getInstance(nonCachedInstanceProviders.get(name));
            return ZkServiceInstanceAdaptor.groupInstance(instance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<GroupInstance> all(String name) {
        try {
            List<ServiceInstance<GroupProxy>> instances = serviceProviders.get(name).getAllInstances();
            if (instances.isEmpty())
                instances = nonCachedInstanceProviders.get(name).getInstances();
            List<GroupInstance> groupInstances = new ArrayList<>();
            for (ServiceInstance<GroupProxy> instance : instances) {
                groupInstances.add(ZkServiceInstanceAdaptor.groupInstance(instance));
            }
            return groupInstances;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GroupInstance firstInstance(String name) {
        try {
            Collection<ServiceInstance<GroupProxy>> instances = serviceProviders.get(name).getAllInstances();
            if (instances.isEmpty())
                instances = nonCachedInstanceProviders.get(name).getInstances();
            if (instances.isEmpty()) return null;
            return ZkServiceInstanceAdaptor.groupInstance(instances.iterator().next());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GroupInstance instance(String groupInstanceId) {
        String groupName = new GroupInstanceIdBean(groupInstanceId).getGroup();
        ServiceInstance<GroupProxy> instance = serviceProviders.getUnchecked(groupName).getInstance(groupInstanceId);
        if (instance == null)
            instance = nonCachedInstanceProviders.getUnchecked(groupName).getInstance(groupInstanceId);
        return ZkServiceInstanceAdaptor.groupInstance(instance);
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
    public GroupProxy newestDefinition(String name) {
        return serviceProviders.getUnchecked(name).getNewestServiceDefinition();
    }

}
