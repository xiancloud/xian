package info.xiancloud.plugin.zookeeper.service_discovery_new.unit;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.LocalUnitsManager;
import info.xiancloud.plugin.distribution.UnitProxy;
import info.xiancloud.plugin.distribution.LocalNodeManager;
import info.xiancloud.plugin.distribution.Node;
import info.xiancloud.plugin.distribution.service_discovery.UnitDiscovery;
import info.xiancloud.plugin.distribution.service_discovery.UnitInstance;
import info.xiancloud.plugin.distribution.service_discovery.UnitInstanceIdBean;
import info.xiancloud.plugin.util.EnvUtil;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.zookeeper.ZkConnection;
import info.xiancloud.plugin.zookeeper.ZkPathManager;
import info.xiancloud.plugin.zookeeper.service_discovery_new.ZkServiceInstanceAdaptor;
import org.apache.curator.x.discovery.*;
import org.apache.curator.x.discovery.details.FastjsonServiceDefinitionSerializer;
import org.apache.curator.x.discovery.details.InstanceProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author happyyangyuan
 */
public class ZkUnitDiscovery implements UnitDiscovery {

    private ServiceDiscovery<UnitProxy> serviceDiscovery;
    private LoadingCache<String, ServiceProvider<UnitProxy>> serviceProviders = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)//30分钟后没人使用就释放这个监听器
            .removalListener((RemovalListener<String, ServiceProvider<UnitProxy>>) notification -> {
                try {
                    //释放watcher
                    notification.getValue().close();
                } catch (IOException e) {
                    LOG.error(e);
                }
            })
            .build(new CacheLoader<String, ServiceProvider<UnitProxy>>() {
                @Override
                public ServiceProvider<UnitProxy> load(String key) throws Exception {
                    ServiceProvider<UnitProxy> serviceProvider = serviceDiscovery.serviceProviderBuilder()
                            .serviceName(key)
                            .build();
                    serviceProvider.start();
                    return serviceProvider;
                }
            });
    private LoadingCache<String, InstanceProvider<UnitProxy>> nonCachedInstanceProviders = CacheBuilder.newBuilder()
            .build(new CacheLoader<String, InstanceProvider<UnitProxy>>() {
                @Override
                public InstanceProvider<UnitProxy> load(String unitFullName) throws Exception {
                    return new InstanceProvider<UnitProxy>() {
                        @Override
                        public List<ServiceInstance<UnitProxy>> getInstances() throws Exception {
                            return (List<ServiceInstance<UnitProxy>>) serviceDiscovery.queryForInstances(unitFullName);
                        }

                        @Override
                        public ServiceInstance<UnitProxy> getInstance(String id) {
                            try {
                                return serviceDiscovery.queryForInstance(unitFullName, id);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    };
                }
            });

    public void init() {
        serviceDiscovery = ServiceDiscoveryBuilder.builder(UnitProxy.class)
                .basePath(ZkPathManager.getUnitBasePath())
                .serializer(new ZkUnitInstanceSerializer())
                .serializer(new FastjsonServiceDefinitionSerializer<>(UnitProxy.class))
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
     * 向zk server注册本节点内所有unit；
     * 注意，必须先init然后才可以执行本方法
     */
    public void register() {
        LocalUnitsManager.searchUnitMap(new Consumer<Map<String, Unit>>() {
            @Override
            public void accept(Map<String, Unit> searchUnitMap) {
                searchUnitMap.forEach((unitFullName, unit) -> {
                    try {
                        ServiceInstance<UnitProxy> thisInstance = thisInstance(unitFullName, unit);
                        serviceDiscovery.registerService(thisInstance);
                    } catch (Exception e) {
                        LOG.error(e);//别打断循环内其他操作
                    }
                });
            }
        });
    }

    /**
     * 注销本节点内所有unit实例
     */
    public void unregister() {
        LocalUnitsManager.searchUnitMap(searchUnitMap ->
                searchUnitMap.forEach((unitFullName, unit) -> {
                    try {
                        ServiceInstance<UnitProxy> thisInstance = thisInstance(unitFullName, unit);
                        serviceDiscovery.unregisterService(thisInstance);
                    } catch (Throwable e) {
                        LOG.error(e);//别打断循环内其他操作
                    }
                }));
    }

    private static ServiceInstance<UnitProxy> thisInstance(String unitFullName, Unit unit) throws Exception {
        return ServiceInstance.<UnitProxy>builder()
                .address(EnvUtil.getLocalIp())
                .enabled(true)
                .id(new UnitInstanceIdBean(unitFullName, LocalNodeManager.LOCAL_NODE_ID).getUnitInstanceId())
                .name(unitFullName)
                .port(Node.RPC_PORT)
                .payload(UnitProxy.create(unit))
                .serviceType(ServiceType.DYNAMIC)
                .build();
    }

    @Override
    public UnitInstance lb(String name) {
        try {
            ServiceInstance<UnitProxy> instance = serviceProviders.get(name).getInstance();
            if (instance == null)
                instance = serviceProviders.get(name).getProviderStrategy()
                        .getInstance(nonCachedInstanceProviders.get(name));
            return ZkServiceInstanceAdaptor.unitInstance(instance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UnitInstance> all(String unitFullName) {
        Collection<ServiceInstance<UnitProxy>> instances;
        try {
            instances = serviceProviders.get(unitFullName).getAllInstances();
            if (instances.isEmpty()) {
                instances = nonCachedInstanceProviders.get(unitFullName).getInstances();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<UnitInstance> unitInstances = new ArrayList<>();
        for (ServiceInstance<UnitProxy> instance : instances) {
            unitInstances.add(ZkServiceInstanceAdaptor.unitInstance(instance));
        }
        return unitInstances;
    }

    @Override
    public UnitInstance firstInstance(String unitFullName) {
        try {
            Collection<ServiceInstance<UnitProxy>> instances = serviceProviders.getUnchecked(unitFullName).getAllInstances();
            if (instances.isEmpty())
                instances = nonCachedInstanceProviders.get(unitFullName).getInstances();
            if (instances.isEmpty()) return null;
            ServiceInstance<UnitProxy> zkServiceInstance = instances.iterator().next();
            return ZkServiceInstanceAdaptor.unitInstance(zkServiceInstance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UnitInstance instance(String unitInstanceId) {
        String name = new UnitInstanceIdBean(unitInstanceId).getFullName();
        ServiceInstance<UnitProxy> serviceInstance = serviceProviders.getUnchecked(name).getInstance(unitInstanceId);
        if (serviceInstance == null)
            serviceInstance = nonCachedInstanceProviders.getUnchecked(name).getInstance(unitInstanceId);
        return ZkServiceInstanceAdaptor.unitInstance(serviceInstance);
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
    public UnitProxy newestDefinition(String unitFullName) {
        return serviceProviders.getUnchecked(unitFullName).getNewestServiceDefinition();
    }
}
