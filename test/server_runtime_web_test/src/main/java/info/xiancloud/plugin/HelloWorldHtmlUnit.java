package info.xiancloud.plugin;

import info.xiancloud.plugin.message.HttpContentType;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.test.TestGroup;
import info.xiancloud.plugin.util.LOG;

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
    public UnitResponse execute(UnitRequest msg) {
        LOG.info(msg.getArgMap());
        return UnitResponse.success("<html><body><h1>hello world.</h1></body></html>")
                .setContext(UnitResponse.Context.create().setHttpContentType(HttpContentType.TEXT_HTML));
    }
}
