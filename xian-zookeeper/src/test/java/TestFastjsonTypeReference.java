import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import info.xiancloud.core.distribution.NodeStatus;
import org.apache.curator.x.discovery.ServiceInstance;
import org.junit.Assert;
import org.junit.Test;

public class TestFastjsonTypeReference {
    @Test
    public void test() {
        final NodeStatus status = new NodeStatus();
        status.setActiveCount(1);
        ServiceInstance<NodeStatus> instance = JSON.parseObject(new JSONObject() {{
            put("payload", status);
        }}.toJSONString(), new TypeReference<ServiceInstance<NodeStatus>>() {
        });
        Assert.assertEquals(instance.getPayload().getActiveCount(), 1);
    }
}
