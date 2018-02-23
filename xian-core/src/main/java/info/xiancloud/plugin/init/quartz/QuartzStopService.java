package info.xiancloud.plugin.init.quartz;

import info.xiancloud.plugin.init.shutdown.ShutdownHook;
import info.xiancloud.plugin.quartz.QuartzLauncher;

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
