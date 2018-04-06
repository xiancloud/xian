package info.xiancloud.core.test.output_test;

import info.xiancloud.core.message.SingleRxXian;

/**
 * @author happyyangyuan
 */
public class OutputTestMain {
    public static void main(String... args) {
        SingleRxXian
                .call("test", "UnitResponseTestUnit")
                .subscribe(unitResponse -> System.out.println(unitResponse.toVoJSONString()));
    }
}
