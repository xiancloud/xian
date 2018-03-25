package info.xiancloud.yy.send_local_stream;

import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.Xian;
import info.xiancloud.plugins.yy.stream_rpc.StreamRpcTestReqUnit;

/**
 * @author happyyangyuan
 */
public class SendLocalStreamTest {
    public static void main(String[] args) {
        Xian.call(StreamRpcTestReqUnit.class, new NotifyHandler() {
            @Override
            protected void handle(UnitResponse unitResponse) {
            }
        });
    }
}
