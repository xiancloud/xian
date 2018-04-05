package info.xiancloud.core.message.sender;

import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * stateful sender future.
 *
 * @author happyyangyuan
 */
public class SenderFuture implements Future<UnitResponse> {

    private final Object responseLock = new Object();
    private volatile UnitResponse unitResponse;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        LOG.debug("cancellation is not supported，thanks!");
        return false;
    }

    @Override
    public boolean isCancelled() {
        LOG.debug("cancellation is not supported，thanks!");
        return false;
    }

    @Override
    public boolean isDone() {
        return unitResponse != null;
    }

    /**
     * Note, this method will return right after finishing execution of the unit and before the remote handler is called.
     * This method waits and never times out.
     *
     * @return the unit response
     */
    public UnitResponse get() {
        synchronized (responseLock) {
            while (!isDone())
                try {
                    responseLock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            return unitResponse;
        }
    }

    @Override
    public UnitResponse get(long timeout, TimeUnit unit) throws TimeoutException {
        if (timeout <= 0) {
            throw new IllegalArgumentException("超时时间必须为正数：" + timeout);
        }
        long timeoutInMilliseconds = unit.toMillis(timeout);
        synchronized (responseLock) {
            while (!isDone()) {
                try {
                    responseLock.wait(timeoutInMilliseconds);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (!isDone()) {
                    throw new TimeoutException("任务超时" + timeout + unit.name());
                }
            }
        }
        return unitResponse;
    }

    void setUnitResponse(UnitResponse unitResponse) {
        synchronized (responseLock) {
            this.unitResponse = unitResponse;
            if (unitResponse != null)
                responseLock.notifyAll();
        }
    }

}
