package info.xiancloud.plugin.executor;

import info.xiancloud.plugin.handle.TransactionalNotifyHandler;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.Xian;
import info.xiancloud.plugin.util.LOG;

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
        Xian.call(controllerRequest, handler);
    }

}
