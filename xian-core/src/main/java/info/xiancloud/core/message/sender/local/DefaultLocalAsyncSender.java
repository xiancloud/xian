package info.xiancloud.core.message.sender.local;

import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.NotifyHandler;

/**
 * 默认本地unit消息发送器，非定向型(routed=false)
 *
 * @author happyyangyuan
 */
public class DefaultLocalAsyncSender extends AbstractLocalAsyncSender {

    public DefaultLocalAsyncSender(UnitRequest request, NotifyHandler callback) {
        super(request, callback);
    }

}
