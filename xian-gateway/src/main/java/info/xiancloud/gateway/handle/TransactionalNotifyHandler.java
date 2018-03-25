package info.xiancloud.gateway.handle;

import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.message.UnitResponse;

/**
 * @author happyyangyuan
 */
public abstract class TransactionalNotifyHandler extends NotifyHandler {

    private boolean transactional;

    public boolean isTransactional() {
        return transactional;
    }

    public TransactionalNotifyHandler setTransactional(boolean transactional) {
        this.transactional = transactional;
        return this;
    }

    private void endTransaction(UnitResponse o) {
        if (o.succeeded()) {
            /*TransactionalCache.commitDistributedTrans();*/
        } else {
            /*TransactionalCache.rollbackDistributedTrans();*/
        }
    }

    @Override
    public void callback(UnitResponse unitResponseObject) {
        if (isTransactional()) {
            endTransaction(unitResponseObject);
        }
        super.callback(unitResponseObject);
    }
}
