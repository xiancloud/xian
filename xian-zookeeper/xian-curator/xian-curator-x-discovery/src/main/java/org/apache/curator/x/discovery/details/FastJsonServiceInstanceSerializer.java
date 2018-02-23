package org.apache.curator.x.discovery.details;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.curator.x.discovery.ServiceInstance;

import java.lang.reflect.Type;

/**
 * fastjson版的序列化
 *
 * @author happyyangyuan
 * @deprecated 有点问题，还用不了；以后解决！
 */
public class FastJsonServiceInstanceSerializer<T> implements InstanceSerializer<T> {

    private Class<ServiceInstance<T>> tClass;

    public FastJsonServiceInstanceSerializer(Class<ServiceInstance<T>> tClass) {
        this.tClass = tClass;
    }

    @Override
    public byte[] serialize(ServiceInstance<T> instance) {
        return JSON.toJSONBytes(instance);
    }

    @Override
    public ServiceInstance<T> deserialize(byte[] bytes) {
        return JSON.parseObject(new String(bytes), new TypeReference<ServiceInstance<T>>() {
            @Override
            public Type getType() {
                return tClass.getComponentType();
            }
        });
    }
}
