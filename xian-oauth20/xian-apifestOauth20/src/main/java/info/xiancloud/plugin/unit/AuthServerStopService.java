package info.xiancloud.plugin.unit;

import com.apifest.oauth20.OAuthServer;
import info.xiancloud.plugin.init.shutdown.ShutdownHook;
import info.xiancloud.plugin.util.LOG;

/**
 * @author happyyangyuan
 */
public class AuthServerStopService implements ShutdownHook {

    @Override
    public boolean shutdown() {
        try {
            OAuthServer.singletonServer.stopServer();
            return true;
        } catch (Throwable e) {
            LOG.error(e);
            return false;
        }
    }

}
