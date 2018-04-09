package info.xiancloud.plugin;

import info.xiancloud.core.*;
import info.xiancloud.core.message.HttpContentType;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.test.TestGroup;
import info.xiancloud.core.util.LOG;

/**
 * @author happyyangyuan
 */
public class HelloWorldHtmlUnit implements Unit {
    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDataOnly(true);
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        LOG.info(msg.getArgMap());
        UnitResponse unitResponse = UnitResponse.createSuccess("<html><body><h1>hello world.</h1></body></html>")
                .setContext(UnitResponse.Context.create().setHttpContentType(HttpContentType.TEXT_HTML));
        handler.handle(unitResponse);
    }
}
