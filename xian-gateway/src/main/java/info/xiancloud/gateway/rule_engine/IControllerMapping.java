package info.xiancloud.gateway.rule_engine;

import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.util.Reflection;
import info.xiancloud.gateway.executor.BaseController;
import info.xiancloud.gateway.executor.UnitController;
import info.xiancloud.gateway.handle.TransactionalNotifyHandler;

import java.util.Collections;
import java.util.List;

/**
 * request uri mappingRuleController for a  {@link BaseController task}
 *
 * @author happyyangyuan
 */
public interface IControllerMapping {

    List<IControllerMapping> uriMappings = Collections.unmodifiableList(Reflection.getSubClassInstances(IControllerMapping.class));
    IControllerMapping singleton = uriMappings.isEmpty() ? null : uriMappings.get(0);

    /**
     * Create a runnable controller instance mapping the uri. This is a factory method.
     * Note that the controller is stateful, so subclass implementation must create new instance every time.
     *
     * @param unitRequest the request parameters.
     * @param handler     the call back
     * @return the stateful runnable controller instance.
     */
    static BaseController getController(UnitRequest unitRequest, TransactionalNotifyHandler handler) {
        try {
            //uriBean.getBasePath()
            BaseController runnable = singleton.mappingRuleController(unitRequest, handler);
            if (runnable == null)
                runnable = new UnitController(unitRequest, handler);
            return runnable;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param unitRequest the unit request which contains a parameter map and a context bean.
     * @param handler     the callback
     * @return the mapped task.
     */
    BaseController mappingRuleController(UnitRequest unitRequest, TransactionalNotifyHandler handler);


}
