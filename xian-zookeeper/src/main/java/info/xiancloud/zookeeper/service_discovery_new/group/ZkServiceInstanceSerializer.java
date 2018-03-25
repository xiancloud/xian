package info.xiancloud.zookeeper.service_discovery_new.group;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import info.xiancloud.core.distribution.GroupProxy;
import info.xiancloud.zookeeper.utils.FastJsonServiceInstanceSerializer;
import org.apache.curator.x.discovery.ServiceInstance;

/**
 * @deprecated
 */
public class ZkServiceInstanceSerializer extends FastJsonServiceInstanceSerializer<GroupProxy> {

    @Override
    public ServiceInstance<GroupProxy> deserialize(byte[] bytes) {
        return JSON.parseObject(new String(bytes), new TypeReference<ServiceInstance<GroupProxy>>() {
        });
    }

}
