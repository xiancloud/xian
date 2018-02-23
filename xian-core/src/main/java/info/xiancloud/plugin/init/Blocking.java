package info.xiancloud.plugin.init;

import info.xiancloud.plugin.distribution.exception.ApplicationOfflineException;
import info.xiancloud.plugin.distribution.exception.ApplicationUndefinedException;
import info.xiancloud.plugin.distribution.loadbalance.ApplicationRouter;
import info.xiancloud.plugin.util.EnvUtil;
import info.xiancloud.plugin.util.LOG;

import java.util.HashSet;
import java.util.Set;

/**
 * 阻塞直到受依赖的服务全部启动
 *
 * @author happyyangyuan
 */
public class Blocking {
    //阻塞直到受依赖的组件在本地注册后才可以继续
    public static void blockUntilReady() {
        Set<String> notReady = new HashSet<String>() {{
            addAll(EnvUtil.getDependencies());
        }};
        while (!notReady.isEmpty()) {
            LOG.info("等待被依赖组件'" + notReady + "'...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                LOG.error(e1);
            }
            for (String dependentApplication : EnvUtil.getDependencies()) {
                LOG.debug("这里每次均检查所有被依赖节点，而不是检查notReady集合");
                try {
                    ApplicationRouter.singleton.allInstances(dependentApplication);
                    notReady.remove(dependentApplication);
                } catch (ApplicationOfflineException | ApplicationUndefinedException e) {
                    LOG.info("受依赖组件未启动：" + dependentApplication);
                }
            }
        }
    }
}
