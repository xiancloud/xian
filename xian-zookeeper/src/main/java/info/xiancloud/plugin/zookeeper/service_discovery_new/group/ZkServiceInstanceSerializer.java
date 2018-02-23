package info.xiancloud.plugin.zookeeper.service_discovery_new.group;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import info.xiancloud.plugin.distribution.GroupProxy;
import info.xiancloud.plugin.zookeeper.utils.FastJsonServiceInstanceSerializer;
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
