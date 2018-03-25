package info.xiancloud.core.test.output_test;

import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.Xian;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.Xian;
import info.xiancloud.core.NotifyHandler;

/**
 * @author happyyangyuan
 */
public class OutputTestMain {
    public static void main(String... args) {
        Xian.call("test", "UnitResponseTestUnit", new NotifyHandler() {
            @Override
            protected void handle(UnitResponse unitResponse) {
                System.out.println(unitResponse.toVoJSONString());
            }
        });
    }
}
