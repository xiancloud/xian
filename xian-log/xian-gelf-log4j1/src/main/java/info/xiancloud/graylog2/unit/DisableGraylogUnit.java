package info.xiancloud.graylog2.unit;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.graylog2.GelfLog4j1Init;

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
            return UnitResponse.createSuccess();
        } catch (GelfLog4j1Init.AlreadyDestroyedException e) {
            return UnitResponse.createException(e);
        }
    }

    @Override
    public Group getGroup() {
        return GraylogService.singleton;
    }
}
