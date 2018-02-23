package info.xiancloud.plugin.rules;

import info.xiancloud.plugin.executor.BaseController;
import info.xiancloud.plugin.executor.URIBean;
import info.xiancloud.plugin.handle.TransactionalNotifyHandler;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.rule_engine.IControllerMapping;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.TraverseClasspath;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * RuleControllerRouter, this is the implementation of {@link IControllerMapping}
 *
 * @author happyyangyuan
 */
public class RuleControllerRouter implements IControllerMapping {
    private static volatile Map<String, Class<? extends RuleController>> ruleMap;
    private static final Object lockForLazyInitialization = new Object();

    private static void loadRules() {
        ruleMap = new HashMap<>();
        Set<Class<? extends RuleController>> rules = TraverseClasspath.getNonAbstractSubClasses(RuleController.class);
        LOG.info("RuleControllers found: " + rules);
        for (Class<? extends RuleController> rule : rules) {
            LOG.info("a rule = " + rule.getName());
            //rule.getName() = com.abc.group.unit
            String[] split = rule.getName().split("\\.");
            String unit = split[split.length - 1],
                    group = split[split.length - 2];
            String uri = "/" + group + "/" + unit;
            ruleMap.put(uri, rule);
        }
    }

    /**
     * This method is in fact a factory method which produces the mapped rule controller.
     *
     * @param baseUri the standard uri: /group/unit
     * @return the mapped rule controller or null if not mapped.
     */
    private static RuleController getRule(String baseUri, UnitRequest controllerRequest, TransactionalNotifyHandler handler) {
        if (ruleMap == null) {
            synchronized (lockForLazyInitialization) {
                if (ruleMap == null) {
                    loadRules();
                }
            }
        }
        try {
            if (ruleMap.get(baseUri) != null) {
                Constructor<? extends RuleController> constructor = ruleMap.get(baseUri).getConstructor();
                //rule controller is stateful, so we need to create new instance for each request.
                RuleController controller = constructor.newInstance();
                controller.setHandler(handler.setTransactional(controller.isTransactional()));
                controller.setControllerRequest(controllerRequest);
                LOG.debug("rule found: " + controller.getClass() + " for uri " + baseUri);
                return controller;
            }
            LOG.debug("rule controller not mapped:" + baseUri);
            return null;
        } catch (Throwable e) {
            throw new RuntimeException("error while mapping rule controller for uri " + baseUri, e);
        }
    }

    @Override
    public BaseController mappingRuleController(UnitRequest unitRequest, TransactionalNotifyHandler handler) {
        String baseUri = URIBean.create(unitRequest.getContext().getUri()).getBasePath();
        return getRule(baseUri, unitRequest, handler);
    }
}
