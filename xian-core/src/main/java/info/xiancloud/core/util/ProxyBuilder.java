package info.xiancloud.core.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 代理对象构建器,实现aop
 *
 * @author happyyangyuan
 */
public abstract class ProxyBuilder<Interface> {
    //　这个就是我们要代理的真实对象
    final private Interface originObject;
    final private Class<Interface> interfaceClass;
    final private Interface proxiedTarget;
    final private InvocationHandler invocationHandler;
    /**
     * 代理 和 代理构建器的一一对应关系
     */
    //final private static Map<Object, ProxyBuilder> proxyBuilderMap = new ConcurrentHashMap<>();
    final private static Map<Integer, ProxyBuilder> proxyBuilderMap = new ConcurrentHashMap<>();

    public static <T> ProxyBuilder getProxyBuilder(Integer proxy) {
        return proxyBuilderMap.get(proxy);
    }

    /**
     * 从内存内删除代理对应的构建器
     */
    public static <T> ProxyBuilder<T> removeProxyBuilder(Integer proxy) {
        return proxyBuilderMap.remove(proxy);
    }

    /**
     * 代理构建器默认是不可跟踪的
     */
    public ProxyBuilder(Interface originObject) {
        this(originObject, false);
    }

    /**
     * @param traceable 可指定代理构建器是否可追踪
     */
    public ProxyBuilder(Interface originObject, boolean traceable) {
        this.originObject = originObject;
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        interfaceClass = (Class<Interface>) parameterizedType.getActualTypeArguments()[0];
        invocationHandler = new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object result = null;
                if (Object.class == method.getDeclaringClass()) {
                    LOG.debug("以下是绕开对Object方法的拦截!");
                    String name = method.getName();
                    if ("equals".equals(name)) {
                        return proxy == args[0];
                    } else if ("hashCode".equals(name)) {
                        return System.identityHashCode(proxy);
                    } else if ("toString".equals(name)) {
                        return proxy.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(proxy)) + ", with InvocationHandler " + this;
                    } else {
                        throw new IllegalStateException(String.valueOf(method));
                    }
                } else {
                    LOG.debug("遇到接口申明的方法,开始拦截");
                    Object beforeReturn;
                    boolean exceptionEnd = false;
                    try {
                        beforeReturn = before(method, args);
                    } catch (OriginalResultReplacement resultReplacement) {
                        exceptionEnd = true;
                        if (method.getReturnType().isAssignableFrom(resultReplacement.replacement.getClass())) {
                            result = resultReplacement.replacement;
                            beforeReturn = resultReplacement;
                            LOG.debug("before方法抛出的resultReplacement异常对象作为beforeReturn传递给after方法");
                        } else {
                            Throwable error = new RuntimeException(String.format("[AOP]使用方式错误! %s的构造器入参类型应当为被拦截的方法%s返回类型%s! 而你返回的类型是%s  ",
                                    OriginalResultReplacement.class, method.getName(), method.getReturnType(), resultReplacement.replacement.getClass()),
                                    resultReplacement);
                            LOG.error("以下是抛出一个提示性的异常,方便大家排查", error);
                            throw error;
                        }
                    } catch (Throwable otherE) {
                        LOG.error("[AOP] before方法抛出的其他异常肯定都是开发者代码不够健壮导致的错误,原样抛出,自行调试排查!", otherE);
                        throw otherE;
                    }
                    try {
                        if (!exceptionEnd) {
                            LOG.debug("这里是真正执行原始方法地方");
                            result = method.invoke(originObject, args);
                        }
                    } catch (Throwable e) {
                        LOG.error("被代理对象执行出现异常", e);
                        result = e;
                        LOG.debug("[AOP] 被拦截的方法自己抛出了异常,这里直接将异常对象传递给后置执行'after'方法,然后异常被原样抛出.", e);
                        try {
                            after(method, args, result, beforeReturn);
                        } catch (Throwable afterE) {
                            LOG.error("[AOP] after方法有异常抛出!请检查!", afterE);
                        }
                        LOG.debug("[AOP 友情提示] 即使原始方法invoke出现异常,也必须要执行after方法");
                        throw e;
                    }
                    try {
                        LOG.debug("原始方法执行时没有抛出异常,那么识别after的OriginalResultReplacement结果");
                        after(method, args, result, beforeReturn);
                    } catch (OriginalResultReplacement resultReplacement) {
                        if (method.getReturnType().isAssignableFrom(resultReplacement.replacement.getClass())) {
                            result = resultReplacement.replacement;
                        } else {
                            LOG.debug("以下是抛出一个提示性的异常,方便大家排查");
                            throw new RuntimeException(String.format("[AOP]使用方式错误! %s的构造器入参类型应当为被拦截的方法%s返回类型%s! 而你返回的类型是%s  ",
                                    OriginalResultReplacement.class, method.getName(), method.getReturnType(), resultReplacement.replacement.getClass()),
                                    resultReplacement);
                        }
                    } catch (Throwable otherE) {
                        LOG.error("[AOP] after方法抛出的其他异常肯定都是开发者代码不够健壮导致的错误,原样抛出,自行调试排查!", otherE);
                        throw otherE;
                    }
                }
                return result;
            }
        };
        proxiedTarget = (Interface) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{interfaceClass}, invocationHandler);
        if (traceable) {
            proxyBuilderMap.put(proxiedTarget.hashCode(), this);
        }
    }

    /**
     * @param method 被拦截的方法
     * @param args   被拦截的方法的入参
     * @return 返回结果将会被传入after方法
     */
    abstract public Object before(Method method, Object[] args) throws OriginalResultReplacement;

    /**
     * @param method       被拦截的方法
     * @param methodReturn 被拦截的方法执行后的返回结果
     * @param args         被拦截的方法入参
     * @param beforeReturn before方法的返回结果
     */
    abstract public void after(Method method, Object[] args, Object methodReturn, Object beforeReturn) throws OriginalResultReplacement;

    /**
     * @return 代理对象
     */
    public Interface getProxy() {
        return proxiedTarget;
    }

    /**
     * @return 被代理的那个对象
     */
    public Interface getOriginalTarget() {
        return originObject;
    }

    /**
     * 针对多级代理,返回最最最原始的那个对象
     */
    public Interface getMostOriginalObject() {
        Interface origin = getOriginalTarget();
        while (Proxy.isProxyClass(origin.getClass())) {
            origin = (Interface) getProxyBuilder(origin.hashCode()).getOriginalTarget();
        }
        return origin;
    }

    /**
     * 在拦截前后,可以抛出该异常实现  不返回目标方法执行结果给调用方,而是返回before/after的return的结果!
     */
    public static class OriginalResultReplacement extends Exception {
        Object replacement;

        public OriginalResultReplacement(Object replacement) {
            if (replacement == null) {
                throw new RuntimeException("入参replacement不允许为空!");
            }
            this.replacement = replacement;
        }
    }

    public static void main(String... args) {
        List<Integer> list = new ArrayList<>();
        list.add(100);
        List proxy = new ProxyBuilder<List>(list) {
            @Override
            public Object before(Method method, Object[] args) {
                System.out.println("before : " + method);
                return System.currentTimeMillis();
            }

            @Override
            public void after(Method method, Object[] args, Object returned, Object beforeReturn) {
                for (Object arg : args == null ? new Object[0] : args) {
                    System.out.println(arg);
                }
                System.out.println("after : " + method);
            }
        }.getProxy();
        proxy.add(1);
        List nn = (List) proxy;
        System.out.println(nn);
    }
}

