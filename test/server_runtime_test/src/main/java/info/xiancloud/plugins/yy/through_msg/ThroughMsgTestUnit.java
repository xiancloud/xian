package info.xiancloud.plugins.yy.through_msg;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.test.TestGroup;

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
        return UnitResponse.success(new JSONObject() {{
            put("$throughMsg", "what you see what you get.");
        }});
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }
}
