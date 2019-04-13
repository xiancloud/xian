package info.xiancloud.yy;

import org.junit.Assert;

import java.util.Arrays;

import static info.xiancloud.core.util.ArrayUtil.concatV2;

public class ArrayUtilTest {
    public void testConcatV2() {
        Assert.assertTrue("[1, 2]".equals(Arrays.toString(concatV2(Integer.class, new Integer[]{1}, new Integer[]{2}, null))));
    }
}
