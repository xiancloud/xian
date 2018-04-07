package info.xiancloud.gateway.executor;

import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.util.LOG;
import info.xiancloud.gateway.handle.TransactionalNotifyHandler;

/**
 * unit controller class for the uris which map to units
 *
 * @author happyyangyuan
 */
public class UnitController extends BaseController {

    public UnitController(UnitRequest unitRequest, TransactionalNotifyHandler handler) {
        handler.setTransactional(false);
        this.setHandler(handler);
        this.setControllerRequest(unitRequest);
    }

    @Override
    protected void atomicAsyncRun() {
        LOG.debug("以下消息发送/接收/处理的异常统一抛出到父类BaseExecutor内处理");
        SingleRxXian.call(controllerRequest).subscribe(unitResponse -> handler.callback(unitResponse));
    }

}
