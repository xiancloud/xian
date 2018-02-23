package info.xiancloud.plugin.netty;

import info.xiancloud.plugin.init.IStartService;
import info.xiancloud.plugin.util.LOG;

/**
 * @author happyyangyuan
 */
public class StartNettyServer implements IStartService {

    static NettyServer nettyServerSingleton;

    @Override
    public boolean startup() {
        synchronized (NettyServer.HTTP_SERVER_START_STOP_LOCK) {
            try {
                nettyServerSingleton = new NettyServer();
                nettyServerSingleton.startServer();
                return true;
            } catch (Throwable e) {
                LOG.error(e);
                return false;
            }
        }
    }

}
