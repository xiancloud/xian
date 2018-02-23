package info.xiancloud.yy.send_local_stream;

import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.Xian;
import info.xiancloud.plugin.support.mq.mqtt.handle.NotifyHandler;
import info.xiancloud.plugins.yy.stream_rpc.StreamRpcTestReqUnit;

/**
 * @author happyyangyuan
 */
public class SendLocalStreamTest {
    public static void main(String[] args) {
        Xian.call(StreamRpcTestReqUnit.class, new NotifyHandler() {
            @Override
            protected void toContinue(UnitResponse unitResponse) {
            }
        });
    }
}
