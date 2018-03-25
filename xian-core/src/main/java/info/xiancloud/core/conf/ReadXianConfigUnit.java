package info.xiancloud.core.conf;

import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.util.StringUtil;

/**
 * @author happyyangyuan
 */
public class ReadXianConfigUnit implements Unit {
    @Override
    public Group getGroup() {
        return XianConfigGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("Read xian config");
    }

    @Override
    public String getName() {
        return "readXianConfig";
    }

    @Override
    public Input getInput() {
        return new Input().add("key", String.class, "The configuration key", REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String value = XianConfig.get(msg.get("key"));
        if (StringUtil.isEmpty(value))
            return UnitResponse.dataDoesNotExists(msg.getString("key"), "config not found.");
        else
            return UnitResponse.success(value);
    }
}
