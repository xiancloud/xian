package info.xiancloud.core.init.quartz;

import info.xiancloud.core.init.shutdown.ShutdownHook;
import info.xiancloud.core.support.quartz.QuartzLauncher;
import info.xiancloud.core.init.shutdown.ShutdownHook;
import info.xiancloud.core.support.quartz.QuartzLauncher;

/**
 * @author happyyangyuan
 */
public class QuartzStopService implements ShutdownHook {
    @Override
    public boolean shutdown() {
        QuartzLauncher.stop();
        return true;
    }
}
