package info.xiancloud.plugin.netty.http.trace;

import info.xiancloud.plugin.Constant;
import info.xiancloud.plugin.util.StringUtil;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

/**
 * 辅助类，用于处理外来的msgId，让它可以与内部msgId串联
 *
 * @author happyyangyuan
 */
public class OuterMsgId {
    /**
     * 从httpRequest的Header内摘取出msgId，处于安全考虑，我们只摘取standalone节点传入的msgId <br>
     * 如果取不到那么返回null
     */
    public static String get(FullHttpRequest fullHttpRequest) {
        HttpHeaders httpHeaders = fullHttpRequest.headers();
        if (isNodeLegal(httpHeaders) && !StringUtil.isEmpty(httpHeaders.get(Constant.XIAN_MSG_ID_HEADER))) {
            return httpHeaders.get(Constant.XIAN_MSG_ID_HEADER);
        }
        return null;
    }

    private static boolean isNodeLegal(HttpHeaders headers) {
        //todo 我们现在支持了灵活命名application之后，就无法永久性判定某个application名是standalone的了，所以这里放开这个限制，只要传入了XIAN_APPLICATION_HEADER 就通过
        return headers.get(Constant.XIAN_APPLICATION_HEADER) != null;
    }

}
