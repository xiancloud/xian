package info.xiancloud.apifestoauth20;

import com.apifest.oauth20.OAuthServer;
import info.xiancloud.core.init.shutdown.ShutdownHook;
import info.xiancloud.core.util.LOG;

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
