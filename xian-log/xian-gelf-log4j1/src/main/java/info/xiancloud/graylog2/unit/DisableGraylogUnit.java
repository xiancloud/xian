package info.xiancloud.graylog2.unit;

import info.xiancloud.graylog2.GelfLog4j1Init;
import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;

/**
 * @author happyyangyuan
 */
public class DisableGraylogUnit implements Unit {
    @Override
    public String getName() {
        return "disableGraylog";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("disable all nodes to send log stream to the graylog server.")
                .setBroadcast()
                .setPublic(false);
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        try {
            GelfLog4j1Init.destroy();
            return UnitResponse.success();
        } catch (GelfLog4j1Init.AlreadyDestroyedException e) {
            return UnitResponse.exception(e);
        }
    }

    @Override
    public Group getGroup() {
        return GraylogService.singleton;
    }
}
