package info.xiancloud.gateway.test;

import info.xiancloud.gateway.executor.URIBean;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author happyyangyuan
 */
public class URIBeanTest {
    @Test
    public void uriBeanTest() {
        String uri = "/group/unit/extension-1/extension-2?a=1&a=1&a=2&c=&d=";
        URIBean bean = URIBean.create(uri);
        if (!"group".equals(bean.getGroup()))
            throw new RuntimeException("group检测失败");
        if (!"unit".equals(bean.getUnit()))
            throw new RuntimeException("unit检测失败");
        if (!"extension-1/extension-2".equals(bean.getUriExtension()))
            throw new RuntimeException("uriExtension检测失败");
        if (!"".equals(bean.getUriParameters().getString("c")))
            throw new RuntimeException("uriParameters检测失败");
        Assert.assertTrue(bean.getUriParameters().getJSONArray("a").size() == 3);
    }
}
