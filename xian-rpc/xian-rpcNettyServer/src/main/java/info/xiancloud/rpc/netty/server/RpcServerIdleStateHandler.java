package info.xiancloud.rpc.netty.server;

import info.xiancloud.plugin.Constant;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author happyyangyuan
 */
/*@ChannelHandler.Sharable handler是有状态的，不应该共享，请参考这里：
    https://my.oschina.net/xinxingegeya/blog/295577
*/
public class RpcServerIdleStateHandler extends IdleStateHandler {

    /**
     * 保证在业务超时之后再进行rpc长连接回收；20秒+30分钟
     */
    static final long IDLE_TIMEOUT_IN_MILLI = Constant.UNIT_DEFAULT_TIME_OUT_IN_MILLI + 60 * 1000 * 30;

    RpcServerIdleStateHandler() {
        super(0, 0, IDLE_TIMEOUT_IN_MILLI, TimeUnit.MILLISECONDS);
    }

}
