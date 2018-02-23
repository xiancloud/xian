package org.apache.curator.x.discovery.details;

import com.alibaba.fastjson.JSON;

/**
 * @author happyyangyuan
 */
public class FastjsonServiceDefinitionSerializer<T> implements ServiceDefinitionSerializer<T> {

    private Class<T> tClass;

    public FastjsonServiceDefinitionSerializer(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public byte[] serialize(T payloadBean) {
        return JSON.toJSONBytes(payloadBean);
    }

    @Override
    public T deserialize(byte[] payload) {
        return JSON.parseObject(payload, tClass);
    }
}
