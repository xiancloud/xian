package info.xiancloud.yy.send_local_stream;

import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.plugins.yy.stream_rpc.StreamRpcTestReqUnit;

/**
 * @author happyyangyuan
 */
public class SendLocalStreamTest {
    public static void main(String[] args) {
        SingleRxXian.call(StreamRpcTestReqUnit.class).blockingGet();
    }
}
