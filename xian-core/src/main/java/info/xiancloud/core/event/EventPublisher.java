package info.xiancloud.core.event;

import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.util.TraverseClasspath;
import info.xiancloud.core.util.thread.MsgIdHolder;

import java.util.Collections;
import java.util.Set;

/**
 * 事件发布器,为了使用方便,这里实现了单例模式,我们可以直接使用静态方法publish来发布事件,不需要每次new一个新的publisher对象
 *
 * @author happyyangyuan
 */
public class EventPublisher implements IEventPublish {

    //懒加载,只读
    private static Set<IEventListener> listeners;
    private static final Object lock = new Object();
    private static final IEventPublish singletonPublisher = new EventPublisher();

    public static void publish(Object event) {
        singletonPublisher.publishEvent(event);
    }

    @Override
    public void publishEvent(final Object event) {
        if (listeners == null) {
            synchronized (lock) {
                if (listeners == null) {
                    listeners = Collections.unmodifiableSet(TraverseClasspath.getSubclassInstances(IEventListener.class));
                }
            }
        }
        for (final IEventListener listener : listeners) {
            if (listener.getEventClass().isAssignableFrom(event.getClass())) {
                if (listener.async()) {
                    ThreadPoolManager.execute(() -> listener.onEvent(event), MsgIdHolder.get());
                } else {
                    listener.onEvent(event);
                }
            }
        }
    }

}
