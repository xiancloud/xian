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
public class EnableGraylogUnit implements Unit {

    @Override
    public String getName() {
        return "enableGraylog";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("enable the graylog client to send udp log stream to remote graylog server.")
                .setPublic(false)
                .setBroadcast();
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        try {
            GelfLog4j1Init.init0();
            return UnitResponse.success();
        } catch (GelfLog4j1Init.AlreadyInitializedException e) {
            return UnitResponse.exception(e);
        }
    }

    @Override
    public Group getGroup() {
        return GraylogService.singleton;
    }
}
