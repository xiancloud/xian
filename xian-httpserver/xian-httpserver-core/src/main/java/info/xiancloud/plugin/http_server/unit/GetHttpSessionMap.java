package info.xiancloud.plugin.http_server.unit;

import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.UnitMeta;
import info.xiancloud.plugin.distribution.unit.ReceiveAndBroadcast;
import info.xiancloud.plugin.message.UnitRequest;

/**
 * @author happyyangyuan
 */
public class GetHttpSessionMap extends ReceiveAndBroadcast {
    @Override
    public String getName() {
        return "getHttpSessionMap";
    }

    @Override
    public Group getGroup() {
        return HttpServerGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("获取业务网关内当前未处理的sessionMap内容");
    }

    @Override
    protected UnitResponse execute0(UnitRequest msg) {
        return UnitResponse.success(HttpSessionLocalCache.getSessionMap());
    }

    @Override
    protected boolean async() {
        return false;
    }

    @Override
    protected boolean successDataOnly() {
        return true;
    }
}
