package info.xiancloud.yy;

import info.xiancloud.plugin.util.RetryUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Callable;

/**
 * @author happyyangyuan
 */
public class TestRetryUtil {

    private static volatile int tryTime = 0;

    @Test
    public void testRetryUtil01() throws Throwable {
        String result = "终于成功了";
        Assert.assertEquals(result, RetryUtil.retryUntilNoException(new Callable<String>() {
            @Override
            public String call() throws Exception {
                tryTime++;
                System.out.println(tryTime);
                if (tryTime != 10)
                    throw new IllegalArgumentException();
                return result;
            }
        }, 100, IllegalArgumentException.class));
        Assert.assertEquals(10, tryTime);
    }


    @Test
    public void testRetryUtil02() throws Throwable {
        String result = "终于成功了";
        Assert.assertEquals(result, RetryUtil.retryUntilNoException(new Callable<String>() {
            @Override
            public String call() throws Exception {
                tryTime++;
                System.out.println(tryTime);
                if (tryTime != 10)
                    throw new RuntimeException("fff");
                return result;
            }
        }, 100));
        Assert.assertEquals(10, tryTime);
    }


}
