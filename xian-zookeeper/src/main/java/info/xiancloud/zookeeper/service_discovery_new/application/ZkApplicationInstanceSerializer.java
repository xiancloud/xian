package info.xiancloud.zookeeper.service_discovery_new.application;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import info.xiancloud.core.distribution.NodeStatus;
import info.xiancloud.zookeeper.utils.FastJsonServiceInstanceSerializer;
import org.apache.curator.x.discovery.ServiceInstance;

/**
 * @deprecated
 */
public class ZkApplicationInstanceSerializer extends FastJsonServiceInstanceSerializer<NodeStatus> {

    @Override
    public ServiceInstance<NodeStatus> deserialize(byte[] bytes) {
        return JSON.parseObject(new String(bytes), new TypeReference<ServiceInstance<NodeStatus>>() {
        });
    }

}
