package info.xiancloud.graylog2.unit;

import info.xiancloud.core.*;
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
        return UnitMeta.createWithDescription("disable all nodes to send log stream to the graylog server.")
                .setBroadcast()
                .setPublic(false);
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        try {
            GelfLog4j1Init.destroy();
            handler.handle(UnitResponse.createSuccess());
            return;
        } catch (GelfLog4j1Init.AlreadyDestroyedException e) {
            handler.handle(UnitResponse.createException(e));
            return;
        }
    }

    @Override
    public Group getGroup() {
        return GraylogService.singleton;
    }
}
