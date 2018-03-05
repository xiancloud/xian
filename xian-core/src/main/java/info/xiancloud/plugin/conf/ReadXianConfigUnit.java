package info.xiancloud.plugin.conf;

import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.util.StringUtil;

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
