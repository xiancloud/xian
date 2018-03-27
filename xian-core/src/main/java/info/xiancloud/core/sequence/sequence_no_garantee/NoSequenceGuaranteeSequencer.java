package info.xiancloud.core.sequence.sequence_no_garantee;

import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.message.sender.IAsyncSender;
import info.xiancloud.core.sequence.ISequencer;

/**
 * 这是一个不䏻保证消息执行顺序的"消息排序执行器"，
 * 来消息即丢进任务池，不保证任务执行顺序
 *
 * @author happyyangyuan
 */
public class NoSequenceGuaranteeSequencer implements ISequencer {

    @Override
    public void sequence(IAsyncSender asyncSender, NotifyHandler onFailure) {
        asyncSender.send();
    }

}
