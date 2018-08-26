package info.xiancloud.dao.async;

import info.xiancloud.core.thread_pool.ThreadPoolManager;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * xian rx scheduler.
 * Stateful.
 *
 * @author happyyangyuan
 */
public class XianRxJava2Scheduler extends Scheduler {

    private String msgId;

    public String getMsgId() {
        return msgId;
    }

    public Scheduler setMsgId(String msgId) {
        this.msgId = msgId;
        return this;
    }

    @Override
    public Worker createWorker() {
        return new Worker() {
            private Future f;
            private boolean disposed;

            @Override
            public Disposable schedule(Runnable run, long delay, TimeUnit unit) {
                f = ThreadPoolManager.execute(run, msgId);
                return new Disposable() {

                    @Override
                    public void dispose() {
                        disposed = f.cancel(true);
                    }

                    @Override
                    public boolean isDisposed() {
                        return disposed;
                    }
                };
            }

            @Override
            public void dispose() {
                disposed = f.cancel(true);
            }

            @Override
            public boolean isDisposed() {
                return disposed;
            }
        };
    }
}
