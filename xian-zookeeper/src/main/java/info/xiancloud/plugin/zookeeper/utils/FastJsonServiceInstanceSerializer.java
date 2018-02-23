package info.xiancloud.plugin.zookeeper.utils;

import com.alibaba.fastjson.JSON;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.InstanceSerializer;

/**
 * fastjson版的序列化
 *
 * @author happyyangyuan
 * @deprecated let me have a look.
 */
public class FastJsonServiceInstanceSerializer<T> implements InstanceSerializer<T> {

    @Override
    public byte[] serialize(ServiceInstance<T> instance) {
        return JSON.toJSONBytes(instance);
    }

    @Override
    public ServiceInstance<T> deserialize(byte[] bytes) {
        throw new RuntimeException("不支持");
    }
}
