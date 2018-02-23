package info.xiancloud.plugin.support.mq.mqtt.mqtt_callback.sequencer;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.LocalUnitsManager;
import info.xiancloud.plugin.message.LackParamException;
import info.xiancloud.plugin.support.mq.mqtt.handle.NotifyHandler;
import info.xiancloud.plugin.support.mq.mqtt.mqtt_callback.sequencer.default_sequencer.DefaultSequencer;
import info.xiancloud.plugin.support.mq.mqtt.mqtt_callback.sequencer.sequence_no_garantee.NoSequenceGuaranteeSequencer;
import info.xiancloud.plugin.util.LOG;

/**
 * message sequencer under concurrent situation
 *
 * @author happyyangyuan
 */
public interface ISequencer {

    /**
     * 对消息做保序排队，顺序处理。
     * 将任务提交到保序线程内执行
     *
     * @deprecated 此方法还需要调用方自己去处理异常，请使用{@link #sequence(Runnable, NotifyHandler)}替代
     */
    void sequence(Runnable runnable) throws LackParamException;

    void sequence(Runnable runnable, NotifyHandler onFailure);

    static ISequencer build(String group, String unit, JSONObject argMap) {
        Unit unit1 = LocalUnitsManager.getLocalUnit(group, unit);
        Input input = unit1.getInput();
        if (input != null && input.isSequential()) {
            LOG.info("sequential: " + group + "." + unit);
            return new DefaultSequencer(group, unit, argMap);
        } else {
            return new NoSequenceGuaranteeSequencer();
        }
    }
}
