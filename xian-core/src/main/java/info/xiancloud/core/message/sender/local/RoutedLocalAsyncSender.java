package info.xiancloud.core.message.sender.local;

import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.NotifyHandler;

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
