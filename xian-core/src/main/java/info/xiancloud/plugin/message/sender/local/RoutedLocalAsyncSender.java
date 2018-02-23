package info.xiancloud.plugin.message.sender.local;

import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.support.mq.mqtt.handle.NotifyHandler;

/**
 * 定向+本地发送器(routed=true)
 *
 * @author happyyangyuan
 */
public class RoutedLocalAsyncSender extends AbstractLocalAsyncSender {
    public RoutedLocalAsyncSender(UnitRequest request, NotifyHandler callback) {
        super(request, callback);
        request.getContext().setRouted(true);
    }
}
