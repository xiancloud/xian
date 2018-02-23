package org.apache.curator.x.discovery.details;

import com.google.common.base.Preconditions;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.utils.CloseableExecutorService;
import org.apache.curator.x.discovery.ServiceInstance;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author happyyangyuan
 * curator提供的serviceCache满足不了我的需求，因此这里扩展了一个新的实现；
 * 主要是增加了一个nodeCache实现节点定义缓存，在节点全部离线时，该缓存依然生效
 */
public class ServiceCacheImplExt<T> extends ServiceCacheImpl<T> {
    private NodeCache nodeCache;

    ServiceCacheImplExt(ServiceDiscoveryImpl<T> discovery, String name, ThreadFactory threadFactory) {
        this(discovery, name, convertThreadFactory(threadFactory));
    }

    ServiceCacheImplExt(ServiceDiscoveryImpl<T> discovery, String name, CloseableExecutorService executorService) {
        super(discovery, name, executorService);
        nodeCache = new NodeCache(discovery.getClient(), discovery.pathForName(name));
    }

    @Override
    public void start() throws Exception {
        super.start();
        nodeCache.start(true);
    }

    @Override
    public T getNewestServiceDefinition() {
        byte[] currentCachedData = nodeCache.getCurrentData().getData();
        if (currentCachedData == null || currentCachedData.length == 0) {
            return null;
        }
        return discovery.getServiceDefinitionSerializer().deserialize(nodeCache.getCurrentData().getData());
    }

    @Override
    public void close() throws IOException {
        super.close();
        nodeCache.close();
    }


    private static CloseableExecutorService convertThreadFactory(ThreadFactory threadFactory) {
        Preconditions.checkNotNull(threadFactory, "threadFactory cannot be null");
        return new CloseableExecutorService(Executors.newSingleThreadExecutor(threadFactory));
    }

    @Override
    public ServiceInstance<T> getInstance(String id) {
        return instances.get(id);
    }
}
