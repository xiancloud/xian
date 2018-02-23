package org.apache.curator.x.discovery.details;

/**
 * @param <T> the newestDefinition type
 * @author happyyangyuan
 */
public interface ServiceDefinitionSerializer<T> {

    byte[] serialize(T payloadBean);


    T deserialize(byte[] payload);

}
