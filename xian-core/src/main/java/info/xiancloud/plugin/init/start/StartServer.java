package info.xiancloud.plugin.init.start;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.distribution.IRegistry;
import info.xiancloud.plugin.distribution.LocalNodeManager;
import info.xiancloud.plugin.distribution.res.ResInit;
import info.xiancloud.plugin.distribution.service_discovery.ApplicationDiscovery;
import info.xiancloud.plugin.distribution.service_discovery.GroupDiscovery;
import info.xiancloud.plugin.distribution.service_discovery.UnitDiscovery;
import info.xiancloud.plugin.Constant;
import info.xiancloud.plugin.init.Blocking;
import info.xiancloud.plugin.init.IStartService;
import info.xiancloud.plugin.init.shutdown.shutdown_strategy.ShutdownPid;
import info.xiancloud.plugin.init.shutdown.shutdown_strategy.ShutdownPort;
import info.xiancloud.plugin.init.shutdown.shutdown_strategy.ShutdownStrategy;
import info.xiancloud.plugin.log.ICentralizedLogInitializer;
import info.xiancloud.plugin.rpc.RpcServer;
import info.xiancloud.plugin.util.EnvUtil;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.Reflection;

import java.util.List;

/**
 * Main entrance for server runtime to startup the application.
 */
public class StartServer {

    //startup rpc -->  zkBootStrap
    private static final List<IStartService> startServices = sortAsc(Reflection.getSubClassInstances(IStartService.class));

    private static List<IStartService> sortAsc(List<IStartService> startups) {
        startups.sort((o1, o2) -> Float.compare(o1.startupOrdinal(), o2.startupOrdinal()));
        StringBuilder sb = new StringBuilder("START_UP sequence: ");
        for (IStartService startup : startups) {
            sb.append(startup.getClass().getSimpleName()).append("(").append(startup.startupOrdinal()).append(")").append("-->");
        }
        LOG.info(sb);
        return startups;
    }

    /**
     * <p> {@link ShutdownPid send SIGTERM signal to this java process} or {@link ShutdownPort 向shutdown端口发送shutdown命令}  二选一，目前默认是{@link ShutdownPid} </p>
     * <p> {@link ShutdownPid 向pid发送SIGTERM信号} 或者 {@link ShutdownPort 向shutdown端口发送shutdown命令}  二选一，目前默认是{@link ShutdownPid} </p>
     */
    private static ShutdownStrategy strategy = new ShutdownPid();

    public static void main(String[] args) throws Exception {
        try {
            if (ICentralizedLogInitializer.singleton != null)
                ICentralizedLogInitializer.singleton.init();
            LOG.info(new JSONObject() {{
                put("type", "startup");
                put("description", "开始启动 " + EnvUtil.getApplication());
            }});
            if (!EnvUtil.verifyEnvironment()) {
                throw new Exception("生产环境认证失败!");
            }
            strategy.listenForShutdown();
            if (args != null && args.length > 0) {
                for (String arg : args) {
                    LOG.info("启动参数是:" + arg);
                }
            }
            doStart();
        } catch (Throwable e) {//console级别的日志不会写入到日志文件内! 所以捕获异常时为了打印异常日志!
            LOG.error(String.format("启动失败，系统退出exit(%s)", Constant.SYSTEM_EXIT_CODE_FOR_SYS_INIT_ERROR), e);
            System.exit(Constant.SYSTEM_EXIT_CODE_FOR_SYS_INIT_ERROR);//抛出异常，后台程序是无法退出的，必须system.exit强制退出
        } finally {
            LOG.info(new JSONObject() {{
                put("type", "startup");
                put("description", "启动完毕 " + EnvUtil.getApplication());
            }});
        }
    }

    private static void doStart() throws Exception {
        if (IRegistry.singleton != null)
            IRegistry.singleton.init();
        if (ApplicationDiscovery.singleton != null)
            ApplicationDiscovery.singleton.init();
        if (GroupDiscovery.singleton != null)
            GroupDiscovery.singleton.init();
        if (UnitDiscovery.singleton != null)
            UnitDiscovery.singleton.init();
        Blocking.blockUntilReady();
        if (ResInit.singleton != null)
            ResInit.singleton.init();
        if (RpcServer.singleton != null)
            RpcServer.singleton.init();
        LocalNodeManager.init();
        for (IStartService service : startServices) {
            LOG.info("开始执行startService " + service.getInitArgName());
            boolean result = service.startup();
            if (!result) {
                throw new Exception("StartService: " + service.getClass().getSimpleName() + " failed.");
            }
        }
        if (ApplicationDiscovery.singleton != null)
            ApplicationDiscovery.singleton.register();
        if (GroupDiscovery.singleton != null)
            GroupDiscovery.singleton.register();
        if (UnitDiscovery.singleton != null)
            UnitDiscovery.singleton.register();
        ReadySignal.singleton.init();
    }
}
