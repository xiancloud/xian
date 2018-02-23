package info.xiancloud.plugin.init.quartz;

import info.xiancloud.plugin.init.IStartService;
import info.xiancloud.plugin.quartz.QuartzLauncher;

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
