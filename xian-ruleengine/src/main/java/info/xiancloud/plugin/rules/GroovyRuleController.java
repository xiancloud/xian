package info.xiancloud.plugin.rules;

import info.xiancloud.plugin.rules.annotation.First;
import info.xiancloud.plugin.rules.annotation.Params;
import info.xiancloud.plugin.rules.annotation.ReturnParams;
import info.xiancloud.plugin.util.LOG;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * groovy规则脚本的父类
 * <p>
 * Groovy rule controller
 *
 * @author happyyangyuan
 */
public abstract class GroovyRuleController extends RuleController {

    protected void callNext() {
        try {
            getMethod().invoke(this);
        } catch (Throwable e) {
            LOG.error(e);
        }
    }

    //懒加载
    protected String getFirst() {
        if (!rules_first.containsKey(getClass())) {
            for (Method method : getClass().getDeclaredMethods()) {
                if (method.getDeclaredAnnotation(First.class) != null) {
                    rules_first.put(getClass(), method.getName());
                    LOG.info("[RuleController" + getClass().getName() + "]的第一步 = " + method.getName());
                    break;
                }
            }
        }
        return rules_first.get(getClass());
    }

    //懒加载
    protected String getParams() {
        rules_params.putIfAbsent(getClass(), new ConcurrentHashMap<>());//使用并发安全的map,避免系统启动后突然同一个规则被高并发访问出现并发问题
        Map<String, String> methodName_paramsString = rules_params.get(getClass());
        String params = methodName_paramsString.get(next);
        if (params == null) {
            Params paramsAnnotation = getMethod().getDeclaredAnnotation(Params.class);
            LOG.info(String.format("[RuleController] %s 的 params = %s", next, paramsAnnotation == null ? "" : paramsAnnotation.value()));
            methodName_paramsString.put(next, paramsAnnotation == null ? "" : paramsAnnotation.value());
        }
        return methodName_paramsString.get(next);
    }

    //懒加载
    protected String getReturnParams() {
        rules_returnParams.putIfAbsent(getClass(), new ConcurrentHashMap<>());
        Map<String, String> methodName_returnParams = rules_returnParams.get(getClass());
        String returnParams = methodName_returnParams.get(next);
        if (returnParams == null) {
            ReturnParams returnParamsAnnotation = getMethod().getDeclaredAnnotation(ReturnParams.class);
            LOG.info(String.format("[RuleController] %s 的 returnParams = %s", next, returnParamsAnnotation == null ? "" : returnParamsAnnotation.value()));
            methodName_returnParams.put(next, returnParamsAnnotation == null ? "" : returnParamsAnnotation.value());
        }
        return methodName_returnParams.get(next);
    }

    //懒加载
    private Method getMethod() {
        if (rules_methods.get(getClass()) == null) {
            loadMethods();
        }
        Method nextMethod = rules_methods.get(getClass()).get(next);
        if (nextMethod == null) {
            throw new RuntimeException("规则" + getClass().getName() + "中找不到规则步骤:" + next);
        }
        return nextMethod;
    }

    private void loadMethods() {//加载当前类的所有方法
        Map<String, Method> methods = new HashMap<>();
        for (Method method : getClass().getDeclaredMethods()) {
            methods.put(method.getName(), method);
        }
        rules_methods.put(getClass(), methods);
    }

}
