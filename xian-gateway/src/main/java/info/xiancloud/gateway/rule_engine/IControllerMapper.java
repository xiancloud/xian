package info.xiancloud.gateway.rule_engine;

import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.util.Reflection;
import info.xiancloud.gateway.controller.BaseController;
import info.xiancloud.gateway.controller.UnitController;
import info.xiancloud.gateway.handle.TransactionalNotifyHandler;

import java.util.Collections;
import java.util.List;

/**
 * Controller mapper maps request uri to RuleController for a {@link BaseController}
 *
 * @author happyyangyuan
 */
public interface IControllerMapper {

    List<IControllerMapper> URI_MAPPINGS = Collections.unmodifiableList(Reflection.getSubClassInstances(IControllerMapper.class));
    /**
     * controller mapping singleton instance.
     * This can be null if no rule mapper plugin is installed to classpath.
     */
    IControllerMapper SINGLETON = URI_MAPPINGS.isEmpty() ? null : URI_MAPPINGS.get(0);

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
            BaseController runnable = null;
            if (SINGLETON != null) {
                runnable = SINGLETON.mappingRuleController(unitRequest, handler);
            }
            if (runnable == null) {
                runnable = new UnitController(unitRequest, handler);
            }
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
