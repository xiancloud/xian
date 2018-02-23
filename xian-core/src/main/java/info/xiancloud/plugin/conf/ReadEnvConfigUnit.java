package info.xiancloud.plugin.conf;

import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.util.StringUtil;

/**
 * @author happyyangyuan
 */
public class ReadEnvConfigUnit implements Unit {
    @Override
    public Group getGroup() {
        return EnvConfigGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("Read env config");
    }

    @Override
    public String getName() {
        return "readEnvConfig";
    }

    @Override
    public Input getInput() {
        return new Input().add("key", String.class, "The configuration key", REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String value = EnvConfig.get(msg.get("key"));
        if (StringUtil.isEmpty(value))
            return UnitResponse.dataDoesNotExists(msg.getString("key"), "config not found.");
        else
            return UnitResponse.success(value);
    }
}
