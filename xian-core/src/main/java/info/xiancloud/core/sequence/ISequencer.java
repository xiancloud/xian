package info.xiancloud.core.sequence;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Input;
import info.xiancloud.core.LocalUnitsManager;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.sender.AbstractAsyncSender;
import info.xiancloud.core.sequence.default_sequencer.AsyncSequencer;
import info.xiancloud.core.sequence.sequence_no_garantee.NoSequenceGuaranteeSequencer;
import info.xiancloud.core.util.LOG;

/**
 * message sequencer under concurrent situation
 *
 * @author happyyangyuan
 */
public interface ISequencer {

    /**
     * make the grouped task running in order.
     *
     * @param asyncSender the asynchronous sender called if sequence operation is succeeded.
     * @param onFailure   sequence failure handler called if the sequence operation failed directly
     */
    void sequence(AbstractAsyncSender asyncSender, NotifyHandler onFailure);

    static ISequencer build(String group, String unit, JSONObject argMap) {
        Unit unit1 = LocalUnitsManager.getLocalUnit(group, unit);
        Input input = unit1.getInput();
        if (input != null && input.isSequential()) {
            LOG.info("sequential: " + group + "." + unit);
            return new AsyncSequencer(group, unit, argMap);
        } else {
            return new NoSequenceGuaranteeSequencer();
        }
    }
}
