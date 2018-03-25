package info.xiancloud.gateway.executor;

import info.xiancloud.core.Group;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.transaction.TransactionalCache;
import info.xiancloud.core.util.LOG;
import info.xiancloud.gateway.access_token_validation.ValidateAccessToken;
import info.xiancloud.gateway.handle.TransactionalNotifyHandler;

import java.util.Map;

/**
 * base controller for the uri
 *
 * @author happyyangyuan
 */
public abstract class BaseController implements Runnable {

    /**
     * 原始参数存放在originMap,注意:不要直接将originMap作为map参数传出去,否则可能会被被调方污染originMap的内容
     */
    protected Map<String, Object> originMap;
    protected UnitRequest controllerRequest;
    protected TransactionalNotifyHandler handler;

    @Override
    public void run() {
        try {
            if (!ValidateAccessToken.validate(controllerRequest)) {
                handler.callback(UnitResponse.error(Group.CODE_BAD_REQUEST, null, "请求不合法！"));
            } else {
                if (handler.isTransactional()) {
                    TransactionalCache.beginDistributedTrans();
                }
                atomicAsyncRun();
            }
        } catch (Throwable t) {
            LOG.error(t);
            handler.callback(UnitResponse.exception(t));
        }
    }

    /**
     * 原子操作,根据规则引擎配置来处理业务请求,并将$msgId对应的处理结果返回给http客户端
     */
    protected abstract void atomicAsyncRun();

    public void setHandler(TransactionalNotifyHandler handler) {
        this.handler = handler;
    }

    public void setControllerRequest(UnitRequest controllerRequest) {
        this.controllerRequest = controllerRequest;
        this.originMap = controllerRequest.getArgMap();
    }
}
