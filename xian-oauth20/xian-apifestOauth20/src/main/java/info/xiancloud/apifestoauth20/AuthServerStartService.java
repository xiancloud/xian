package info.xiancloud.apifestoauth20;

import com.apifest.oauth20.OAuthServer;
import info.xiancloud.core.init.IStartService;
import info.xiancloud.core.util.LOG;

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
