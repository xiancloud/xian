package info.xiancloud.plugin.test.output_test;

import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.Xian;
import info.xiancloud.plugin.support.mq.mqtt.handle.NotifyHandler;

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
