package info.xiancloud.yy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.distribution.UnitProxy;
import info.xiancloud.plugin.util.Reflection;
import org.junit.Test;

/**
 * @author happyyangyuan
 */
public class TestDeserializeUnitBean {
    @Test
    public void deserializeUnitBean() {
        String json = "{" +
                "\"input\":{" +
                "\"list\":[" +
                "{" +
                "\"sequential\":false," +
                "\"name\":\"application\"," +
                "\"description\":\"all/null/applicationName  ，  为空或者为all表示全部节点\"," +
                "\"clazz\":\"java.lang.String\"," +
                "\"xhash\":false," +
                "\"required\":false" +
                "}" +
                "]" +
                "}," +
                "\"meta\":{" +
                "\"public\":true," +
                "\"readonly\":false," +
                "\"monitorEnabled\":false," +
                "\"transferable\":false," +
                "\"name\":\"threadPoolMonitor\"," +
                "\"description\":\"线程池使用情况监控\"," +
                "\"scopes\":[" +
                "\"api_all\"" +
                "]," +
                "\"transactional\":false," +
                "}" +
                "}";
        JSONObject jsonObject = JSON.parseObject(json);
        System.out.println(JSON.toJSONString(jsonObject, true));
        UnitProxy proxy = Reflection.toType(jsonObject, UnitProxy.class);
        System.out.println(Reflection.toType(proxy, String.class));
    }
}
