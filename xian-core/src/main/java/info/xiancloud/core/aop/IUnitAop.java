package info.xiancloud.core.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.LocalUnitsManager;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.ProxyBuilder;
import info.xiancloud.core.util.thread.MsgIdHolder;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;

/**
 * unit interception
 *
 * @author happyyangyuan
 * @deprecated this won't work for asynchronous xian
 */
public interface IUnitAop {

    String METHOD_EXECUTE = "execute";

    /**
     * @return 即将被代理的unit集合
     */
    Collection<Unit> getUnitCollection();

    /**
     * intercept
     */
    default void intercept() {
        Collection<Unit> units = getUnitCollection();
        if (units == null || units.isEmpty()) {
            LOG.info("nothing to intercept at all.");
            return;
        }
        final IUnitAop thiz = this;
        for (final Unit originUnit : units) {
            LOG.debug(originUnit.getName() + "    代理对象:" + Proxy.isProxyClass(originUnit.getClass()));
            final Unit nakedUnit;
            if (Proxy.isProxyClass(originUnit.getClass())) {
                nakedUnit = (Unit) ProxyBuilder.getProxyBuilder(originUnit.hashCode()).getMostOriginalObject();
            } else {
                nakedUnit = originUnit;
            }
            Unit proxy = new ProxyBuilder<Unit/**/>(originUnit, true) {
                @Override
                public Object before(Method method, Object[] args) throws UnitResponseReplacement {
                    if (METHOD_EXECUTE.equals(method.getName())) {
                        if (!asyncBefore()) {
                            return thiz.before(nakedUnit, (UnitRequest) args[0]/*, ssn*/);
                        } else {
                            Runnable runnableForBefore = () -> {
                                try {
                                    Object beforeReturnIgnored = thiz.before(nakedUnit, (UnitRequest) args[0]);
                                    LOG.info("异步AOP的前置拦截方法before返回的内容被忽略:" + beforeReturnIgnored);
                                } catch (UnitResponseReplacement outputReplacement) {
                                    throw new RuntimeException("异步aop不允许打断被拦截的方法!", outputReplacement);
                                }
                            };
                            if (trackMsgIdIfAsync()) {
                                ThreadPoolManager.execute(runnableForBefore, MsgIdHolder.get());
                            } else {
                                ThreadPoolManager.executeWithoutTrackingMsgId(runnableForBefore);
                            }
                            return "";
                        }
                    }
                    return "";
                }

                @Override
                public void after(Method method, Object[] args, Object unitReturn, Object beforeReturn) throws UnitResponseReplacement {
                    if (METHOD_EXECUTE.equals(method.getName()) /*&& !beforeReturn.toString().equals(AOP_FILTER_PLAG)*/) {
                        final UnitResponse finalMethodReturn;

                        if (unitReturn instanceof Throwable) {//有异常抛出,则需要回滚
                            finalMethodReturn = UnitResponse.createException((Throwable) unitReturn);
                        } else {
                            finalMethodReturn = (UnitResponse) unitReturn;
                        }

                        /*final AOPSession ssn = createSsn();*/

                        if (!asyncAfter()) {//同步
                            thiz.after(originUnit, (UnitRequest) args[0], finalMethodReturn, beforeReturn/*, ssn*/);
                        } else {
                            Runnable runnableAfter = () -> {
                                try {
                                    thiz.after(originUnit, (UnitRequest) args[0], finalMethodReturn, beforeReturn/*, ssn*/);
                                } catch (UnitResponseReplacement outputReplacement) {
                                    throw new RuntimeException("异步aop不允许执行此操作!", outputReplacement);
                                }
                            };
                            if (trackMsgIdIfAsync()) {
                                ThreadPoolManager.execute(runnableAfter, MsgIdHolder.get());
                            } else {
                                ThreadPoolManager.executeWithoutTrackingMsgId(runnableAfter);
                            }
                        }
                    }
                }
            }.getProxy();
            LocalUnitsManager.replaceUnit(proxy);
        }
    }

    /**
     * 拦截unit,并切入执行前动作
     *
     * @param unit        被拦截的unit原始对象,已经去代理化
     * @param unitRequest 被拦截的unit对象的入参
     * @return 该对象将会被传递至后置执行方法"after"作为其入参,名为"beforeReturn"。
     * @throws UnitResponseReplacement 可通过抛出该异常来阻止目标'execute'方法的执行,unit调用方将会收到这个被抛出的异常对象内的那个UnitResponse对象
     */
    Object before(Unit unit, UnitRequest unitRequest) throws UnitResponseReplacement;

    /**
     * 拦截unit,并切入执行后动作
     *
     * @param unit         被拦截的unit原始对象,已经去代理化
     * @param unitRequest  被拦截的unit对象的入参
     * @param unitResponse 目标'execute'方法的返回结果
     * @param beforeReturn 'before'方法的返回结果
     * @throws UnitResponseReplacement 可通过抛出该异常来替换目标execute方法返回的结果,unit调用方将会收到这个被抛出的异常对象内的那个UnitResponse对象
     */
    void after(Unit unit, UnitRequest unitRequest, UnitResponse unitResponse, Object beforeReturn) throws UnitResponseReplacement;

    /**
     * @return true 执行前动作是否异步; false 同步 (默认);
     */
    default boolean asyncBefore() {
        return false;
    }

    /**
     * @return true 执行后动作是否异步; false 同步 (默认);
     */
    default boolean asyncAfter() {
        return false;
    }

    /**
     * @return 如果是异步aop, 那么此配置决定是否追踪AOP逻辑的消息轨迹，默认不追踪。
     */
    default boolean trackMsgIdIfAsync() {
        return false;
    }

    /**
     * 注销aop拦截
     *
     * @deprecated 该功能未测试，请暂时不要使用
     */
    default void unintercept() {
        Collection<Unit> proxies = getUnitCollection();
        for (Unit proxy : proxies) {
            if (!Proxy.isProxyClass(proxy.getClass())) {
                LOG.warn(proxy.getName() + " 已经不是代理对象了!  不允许重复取消拦截!");
            }
            ProxyBuilder<Unit> proxyBuilder = ProxyBuilder.removeProxyBuilder(proxy.hashCode());
            Unit originUnit = proxyBuilder.getOriginalTarget();
            LocalUnitsManager.replaceUnit(originUnit);
        }
    }


    /**
     * 可以抛出该异常来拦截并替换原始返回结果
     */
    class UnitResponseReplacement extends ProxyBuilder.OriginalResultReplacement {
        public UnitResponseReplacement(UnitResponse replacement) {
            super(replacement);
        }
    }

    static void main(String... args) {
        JSONObject jsonObject = (JSONObject) JSON.toJSON(new RuntimeException("托尔斯泰"));
        System.out.println(jsonObject);
        System.out.println(jsonObject.toJavaObject(RuntimeException.class).getLocalizedMessage());
        LOG.info("得出结论RuntimeException，Throwable都无法用fastjson反序列化");
    }

}
