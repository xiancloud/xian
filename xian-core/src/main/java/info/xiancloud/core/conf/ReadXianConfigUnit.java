package info.xiancloud.core.conf;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.StringUtil;

import java.util.function.Consumer;

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
    public void execute(UnitRequest msg, Consumer<UnitResponse> consumer) {
        String value = XianConfig.get(msg.get("key"));
        if (StringUtil.isEmpty(value))
            consumer.accept(UnitResponse.dataDoesNotExists(msg.getString("key"), "config not found."));
        else
            consumer.accept(UnitResponse.success(value));
    }
}
