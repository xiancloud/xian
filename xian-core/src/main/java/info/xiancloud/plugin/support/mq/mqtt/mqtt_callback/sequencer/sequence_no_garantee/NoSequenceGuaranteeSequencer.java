package info.xiancloud.plugin.support.mq.mqtt.mqtt_callback.sequencer.sequence_no_garantee;

import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.support.mq.mqtt.handle.NotifyHandler;
import info.xiancloud.plugin.support.mq.mqtt.mqtt_callback.sequencer.ISequencer;
import info.xiancloud.plugin.thread_pool.ThreadPoolManager;

import java.util.concurrent.RejectedExecutionException;

/**
 * 这是一个不䏻保证消息执行顺序的"消息排序执行器"，
 * 来消息即丢进任务池，不保证任务执行顺序
 *
 * @author happyyangyuan
 */
public class NoSequenceGuaranteeSequencer implements ISequencer {

    @Override
    public void sequence(Runnable runnable) {
        ThreadPoolManager.execute(runnable);
    }

    @Override
    public void sequence(Runnable runnable, NotifyHandler onFailure) {
        try {
            ThreadPoolManager.execute(runnable);
        } catch (RejectedExecutionException rejectException) {
            onFailure.callback(UnitResponse.failure(null, "Thread pool is full，activeCount=" + ThreadPoolManager.activeCount() + ", execution rejected."));
        }
    }

}
