package info.xiancloud.plugin.support.mq.mqtt.mqtt_callback.sequencer.default_sequencer;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.LocalUnitsManager;
import info.xiancloud.plugin.message.LackParamException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author happyyangyuan
 */
class SequentialDataProvider {

    static Map<String, Object> getSequentialData(String group, String unit, JSONObject argMap) throws LackParamException {
        Map<String, Object> sequentialData = new HashMap<>();
        for (Input.Obj aSequential : LocalUnitsManager.getLocalUnit(group, unit).getInput().getSequential()) {
            String key = aSequential.getName();
            Object value = argMap.get(key);
            if (value == null) {
                throw new LackParamException(group, unit, key);
            }
            sequentialData.put(key, value);
        }
        return sequentialData;
    }
}
