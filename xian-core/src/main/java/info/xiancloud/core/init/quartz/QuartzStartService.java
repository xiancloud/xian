package info.xiancloud.core.init.quartz;

import info.xiancloud.core.support.quartz.QuartzLauncher;
import info.xiancloud.core.init.IStartService;
import info.xiancloud.core.support.quartz.QuartzLauncher;

/**
 * 启动Quartz的RunService.
 *
 * @author explorerlong
 */
public class QuartzStartService implements IStartService {
    @Override
    public boolean startup() {
        return QuartzLauncher.start();
    }

}
