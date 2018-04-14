package info.xiancloud.yy;

import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.test.timeout_test.TimeoutTestUnit;
import org.junit.Test;

public class TimeoutUnitTest {
    @Test
    public void timeoutTest() throws InterruptedException {
        SingleRxXian.call(TimeoutTestUnit.class).blockingGet();
    }
}
