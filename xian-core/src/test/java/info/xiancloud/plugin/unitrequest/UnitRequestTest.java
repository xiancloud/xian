package info.xiancloud.plugin.unitrequest;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Unit;
import info.xiancloud.core.distribution.UnitProxy;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.test.output_test.UnitResponseTestUnit;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author happyyangyuan
 */
public class UnitRequestTest {

    @Test
    public void testGetList() {
        List<Integer> list = new UnitRequest(new JSONObject() {{
            put("yy", new int[]{0, 1});
        }}).getList("yy");
        Assert.assertTrue(list.get(0) == 0);
        Assert.assertTrue(list.get(1) == 1);

        List<Unit> unitList = new UnitRequest(new JSONObject() {{
            put("yy", new Unit[]{new UnitResponseTestUnit()});
        }}).getList("yy");
        Assert.assertTrue(unitList.get(0).getName().equals(new UnitResponseTestUnit().getName()));

        List<UnitProxy> proxyList = new UnitRequest(new JSONObject() {{
            put("yy", new String[]{new UnitResponseTestUnit().toJSONString()});
        }}).getList("yy", UnitProxy.class);
        Assert.assertTrue(proxyList.get(0).getName().equals(new UnitResponseTestUnit().getName()));
    }
}
