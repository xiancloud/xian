package info.xiancloud.yy;

import info.xiancloud.core.util.file.PluginFileUtil;
import org.junit.Assert;
import org.junit.Test;

public class TestPluginFileUtil {
    @Test
    public void testPluginFile() {
        String version = PluginFileUtil.version("a-b-1.23.0-pre.jar");
        System.out.println(version);
        Assert.assertEquals(version, "1.23.0-pre");
        String pluginName = PluginFileUtil.pluginName("a-b-1.23.0-pre.jar");
        System.out.println(pluginName);
        Assert.assertEquals("a-b", pluginName);
    }
}
