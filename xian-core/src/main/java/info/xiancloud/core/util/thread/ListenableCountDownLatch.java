package info.xiancloud.core.util.thread;

import info.xiancloud.core.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * {@link java.util.concurrent.CountDownLatch} with listeners
 * Note that, this class is not thread safe.
 *
 * @author happyyangyuan
 */
public class ListenableCountDownLatch extends CountDownLatch {

    private List<Handler<Long>> listeners = new ArrayList<>();

    /**
     * Constructs a {@code CountDownLatch} initialized with the given count.
     *
     * @param count the number of times {@link #countDown} must be invoked
     *              before threads can pass through {@link #await}
     * @throws IllegalArgumentException if {@code count} is negative
     */
    public ListenableCountDownLatch(int count) {
        super(count);
    }

    /**
     * add a listener.
     * Note that this method is not thread safe.
     *
     * @param handler the listener
     */
    public void addListener(Handler<Long> handler) {
        listeners.add(handler);
    }

    @Override
    public void countDown() {
        super.countDown();
        for (Handler<Long> listener : listeners) {
            listener.handle(getCount());
        }
    }
}
