package info.xiancloud.core.test.echo_test;

import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.test.TestGroup;

/**
 * @author happyyangyuan
 */
public class EchoUnit implements Unit {

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public String getName() {
        return "echoUnit";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setBodyRequired(true);
    }

    @Override
    public void execute(UnitRequest request, Handler<UnitResponse> handler) {
        handler.handle(UnitResponse.createSuccess(request.getContext().getBody()));
    }

}
