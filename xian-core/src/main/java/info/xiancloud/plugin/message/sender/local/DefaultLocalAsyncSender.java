package info.xiancloud.plugin.message.sender.local;

import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.support.mq.mqtt.handle.NotifyHandler;

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
