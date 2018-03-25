package info.xiancloud.core.init.shutdown.shutdown_strategy;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.distribution.IRegistry;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.distribution.res.IResAware;
import info.xiancloud.core.distribution.service_discovery.ApplicationDiscovery;
import info.xiancloud.core.distribution.service_discovery.GroupDiscovery;
import info.xiancloud.core.distribution.service_discovery.UnitDiscovery;
import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.Reflection;
import info.xiancloud.core.util.thread.MsgIdHolder;
import info.xiancloud.core.distribution.IRegistry;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.distribution.res.IResAware;
import info.xiancloud.core.distribution.service_discovery.ApplicationDiscovery;
import info.xiancloud.core.distribution.service_discovery.GroupDiscovery;
import info.xiancloud.core.distribution.service_discovery.UnitDiscovery;
import info.xiancloud.core.init.shutdown.ShutdownHook;
import info.xiancloud.core.init.start.ReadySignal;
import info.xiancloud.core.mq.IMqConsumerClient;
import info.xiancloud.core.mq.IMqPubClient;
import info.xiancloud.core.rpc.RpcClient;
import info.xiancloud.core.rpc.RpcServer;
import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.Reflection;
import info.xiancloud.core.util.thread.MsgIdHolder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author happyyangyuan
 */
public abstract class ShutdownStrategy {

    static String SHUTDOWN = "shutdown";

    //  zkShutdown --> unit shutdowns --> pahoShutdown --> rpcShutdown --> threadPoolShutdown
    private static final List<ShutdownHook> shutdowns = sortAsc(Reflection.getSubClassInstances(ShutdownHook.class));

    private static List<ShutdownHook> sortAsc(List<ShutdownHook> shutdowns) {
        shutdowns.sort((o1, o2) -> {
            if (o1.shutdownOrdinal() > o2.shutdownOrdinal())
                return 1;
            else if (o1.shutdownOrdinal() == o2.shutdownOrdinal())
                return 0;
            else
                return -1;
        });
        StringBuilder sb = new StringBuilder("SHUTDOWN 项目执行顺序:");
        for (ShutdownHook shutdown : shutdowns) {
            sb.append(shutdown.getClass().getSimpleName()).append("(").append(shutdown.shutdownOrdinal()).append(")").append("-->");
        }
        LOG.info(sb);
        return shutdowns;
    }

    public static void addHookToTheEnd(Runnable runnable) {
        shutdowns.add(buildTheHook(runnable));
    }

    public static void addHookBefore(Runnable hookRun, String simpleClassName) {
        int i = 0;
        for (; i < shutdowns.size(); i++) {
            if (shutdowns.get(i) != null) {
                if (shutdowns.get(i).getClass().getSimpleName().equals(simpleClassName)) {
                    break;
                }
            }
        }
        shutdowns.add(i, buildTheHook(hookRun));
    }

    private static ShutdownHook buildTheHook(Runnable hookRun) {
        final String $msgId = MsgIdHolder.get();
        return new ShutdownHook() {
            public boolean shutdown() {
                final String beforeMsgId = MsgIdHolder.get();
                if ($msgId != null) {
                    MsgIdHolder.set($msgId);
                }
                try {
                    hookRun.run();
                    return true;
                } finally {
                    LOG.debug("这里不是clear而是还原");
                    if (beforeMsgId != null) {
                        MsgIdHolder.set(beforeMsgId);
                    } else {
                        MsgIdHolder.clear();
                    }
                }
            }
        };
    }

    /**
     * 系统启动时调用本方法开启shutdown监听
     */
    public void listenForShutdown() {
        registerShutdownHooks();
        prepare();
    }

    private void registerShutdownHooks() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            MsgIdHolder.init();
            long start0 = System.nanoTime();
            LOG.info(new JSONObject() {{
                put("type", SHUTDOWN);
                put("description", "开始执行shutdown " + EnvUtil.getApplication());
            }}.toJSONString());
            boolean shutdownOk = executeShutdownHooks();
            LOG.info(new JSONObject() {{
                put("type", SHUTDOWN);
                put("description", EnvUtil.getApplication() + " 停止完毕");
                put("cost", (System.nanoTime() - start0) / 1000000);
            }});
            if (!shutdownOk) {
                LOG.error("系统异常退出：-1");
                Runtime.getRuntime().halt(-1);
            }
        }));
    }

    abstract protected void prepare();

    private static boolean executeShutdownHooks() {
        final AtomicBoolean success = new AtomicBoolean(true);
        Map<String, Runnable> allShutdowns = new LinkedHashMap<String, Runnable>() /*必须使用有序map */ {{
            put(ReadySignal.class.getSimpleName() + ".destroy", ReadySignal.singleton::destroy);
            if (ApplicationDiscovery.singleton != null)
                put(ApplicationDiscovery.class.getSimpleName() + ".unregister", ApplicationDiscovery.singleton::selfUnregister);
            if (GroupDiscovery.singleton != null)
                put(GroupDiscovery.class.getSimpleName() + ".unregister", GroupDiscovery.singleton::selfUnregister);
            if (UnitDiscovery.singleton != null)
                put(UnitDiscovery.class.getSimpleName() + ".unregister", UnitDiscovery.singleton::selfUnregister);
            for (ShutdownHook hook : shutdowns) {
                put(hook.getClass().getSimpleName(), hook::shutdown);
            }
            put(ThreadPoolManager.class.getSimpleName() + ".destroy", () -> ThreadPoolManager.destroy(9 * 1000));
            LOG.debug("注意顺序：业务线程池销毁完毕代表池内任务线程都已执行完毕，这样方可以继续后续销毁rpc程序。");
            if (RpcClient.singleton != null)
                put(RpcClient.class.getSimpleName() + ".destroy", RpcClient.singleton::destroy);
            if (RpcServer.singleton != null)
                put(RpcServer.class.getSimpleName() + ".destroy", RpcServer.singleton::destroy);
            put(LocalNodeManager.class.getSimpleName() + ".destroy", LocalNodeManager::destroy);
            if (IMqPubClient.singleton != null)
                put(IMqPubClient.singleton.getClass().getSimpleName() + ".destroy", IMqPubClient.singleton::destroy);
            if (IMqConsumerClient.singleton != null && IMqConsumerClient.singleton != IMqPubClient.singleton)
                put(IMqConsumerClient.singleton.getClass().getSimpleName() + ".destroy", IMqConsumerClient.singleton::destroy);
            if (UnitDiscovery.singleton != null)
                put(UnitDiscovery.class.getSimpleName() + ".destroy", UnitDiscovery.singleton::destroy);
            if (GroupDiscovery.singleton != null)
                put(GroupDiscovery.class.getSimpleName() + ".destroy", GroupDiscovery.singleton::destroy);
            if (ApplicationDiscovery.singleton != null)
                put(ApplicationDiscovery.class.getSimpleName() + ".destroy", ApplicationDiscovery.singleton::destroy);
            if (IResAware.singleton != null)
                put(IResAware.class.getSimpleName() + ".destroy", IResAware.singleton::destroy);
            if (IRegistry.singleton != null)
                put(IRegistry.class.getSimpleName() + ".destroy", IRegistry.singleton::destroy);
        }};
        allShutdowns.forEach((name, runnable) -> {
            if (!execWithTimeout(10 * 1000, name, runnable)) {
                success.set(false);
            }
        });
        return success.get();
    }

    private static boolean execWithTimeout(final int timeoutInMilliseconds, final String name, final Runnable task) {
        long start = System.nanoTime();
        Future future = Executors.newSingleThreadExecutor().submit(task);
        try {
            future.get(timeoutInMilliseconds, TimeUnit.MILLISECONDS);
            return true;
        } catch (TimeoutException ignoredE) {
            LOG.error("销毁任务超时：" + timeoutInMilliseconds + " ms，taskName = " + name);
            return false;
        } catch (Throwable e) {
            LOG.error("执行任务 '" + name + "' 时发生错误", e);
            return false;
        } finally {
            LOG.info("销毁 '" + name + "' 用时" + (System.nanoTime() - start) / 1000000 + "ms");
        }
    }


}
