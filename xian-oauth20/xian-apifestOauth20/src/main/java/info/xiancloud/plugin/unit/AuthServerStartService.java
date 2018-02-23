package info.xiancloud.plugin.unit;

import com.apifest.oauth20.OAuthServer;
import info.xiancloud.plugin.init.IStartService;
import info.xiancloud.plugin.util.LOG;

/**
 * @author happyyangyuan
 */
public class AuthServerStartService implements IStartService {

    @Override
    public boolean startup() {
        try {
            OAuthServer.singletonServer.startServer();
            return true;
        } catch (Throwable e) {
            LOG.error(e);
            return false;
        }
    }

}
