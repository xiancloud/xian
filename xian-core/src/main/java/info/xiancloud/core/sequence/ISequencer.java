package info.xiancloud.core.sequence;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.message.sender.IAsyncSender;
import info.xiancloud.core.sequence.default_sequencer.DefaultSequencer;
import info.xiancloud.core.sequence.sequence_no_garantee.NoSequenceGuaranteeSequencer;
import info.xiancloud.core.Unit;
import info.xiancloud.core.Input;
import info.xiancloud.core.LocalUnitsManager;
import info.xiancloud.core.message.LackParamException;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.sequence.default_sequencer.DefaultSequencer;
import info.xiancloud.core.sequence.sequence_no_garantee.NoSequenceGuaranteeSequencer;
import info.xiancloud.core.util.LOG;

/**
 * message sequencer under concurrent situation
 *
 * @author happyyangyuan
 */
public interface ISequencer {

    void sequence(IAsyncSender asyncSender, NotifyHandler onFailure);

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
