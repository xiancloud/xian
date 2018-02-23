package info.xiancloud.yy;

import info.xiancloud.plugin.util.Reflection;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author happyyangyuan
 */
public class TestReflection {
    @Test
    public void testForName() throws ClassNotFoundException {
        Assert.assertTrue(Reflection.forName("int") == int.class);
        Assert.assertTrue(Reflection.ForName.forName("byte") == byte.class);
    }
}
