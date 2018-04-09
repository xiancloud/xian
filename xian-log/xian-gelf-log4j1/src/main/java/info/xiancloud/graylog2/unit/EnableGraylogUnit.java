package info.xiancloud.graylog2.unit;

import info.xiancloud.core.*;
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
        return UnitMeta.createWithDescription("enable the graylog client to send udp log stream to remote graylog server.")
                .setDocApi(false)
                .setBroadcast();
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        try {
            GelfLog4j1Init.init0();
            handler.handle(UnitResponse.createSuccess());
        } catch (GelfLog4j1Init.AlreadyInitializedException e) {
            handler.handle(UnitResponse.createException(e));
        }
    }

    @Override
    public Group getGroup() {
        return GraylogService.singleton;
    }
}
