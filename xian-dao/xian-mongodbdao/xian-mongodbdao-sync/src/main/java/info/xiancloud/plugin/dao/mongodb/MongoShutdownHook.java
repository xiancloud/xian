package info.xiancloud.plugin.dao.mongodb;

import info.xiancloud.core.init.shutdown.ShutdownHook;
import info.xiancloud.core.util.LOG;

/**
 * Mongodb客户端自动销毁的回调钩子
 *
 * @author happyyangyuan
 */
class MongoShutdownHook implements ShutdownHook {
    @Override
    public boolean shutdown() {
        try {
            Mongo.destroy();
            return true;
        } catch (Throwable error) {
            LOG.error(error);
            return false;
        }
    }
}
