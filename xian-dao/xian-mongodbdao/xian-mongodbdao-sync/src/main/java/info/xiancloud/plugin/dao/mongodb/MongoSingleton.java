package info.xiancloud.plugin.dao.mongodb;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mongodb.client.MongoCollection;
import info.xiancloud.core.init.shutdown.ShutdownHook;

import java.util.concurrent.ExecutionException;

/**
 * @deprecated 单例模式的mongodbCollection对象，未经过充分测试
 */
public class MongoSingleton {
    private static Cache<String, MongoCollection> COLLECTION_CACHE_MAP = CacheBuilder.newBuilder().build();

    public static <T> MongoCollection<T> getCollection(String collectionName, Class<T> tClass) {
        try {
            //单例模式，我怕它不是并发安全的
            return COLLECTION_CACHE_MAP.get(collectionName, () -> Mongo.getOrInitDefaultDatabase().getCollection(collectionName, tClass));
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }

}
