package info.xiancloud.plugins.yy.through_msg;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.test.TestGroup;

/**
 * @author happyyangyuan
 */
public class ThroughMsgTestUnit implements Unit {
    @Override
    public String getName() {
        return "throughMsgTest";
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        return UnitResponse.createSuccess(new JSONObject() {{
            put("$throughMsg", "what you see what you get.");
        }});
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
