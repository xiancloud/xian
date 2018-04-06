package info.xiancloud.httpserver.core.unit;

import info.xiancloud.core.Group;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.distribution.unit.ReceiveAndBroadcast;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;

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
        return UnitMeta.createWithDescription("获取业务网关内当前未处理的sessionMap内容");
    }

    @Override
    protected UnitResponse execute0(UnitRequest msg) {
        return UnitResponse.createSuccess(HttpSessionLocalCache.getSessionMap());
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
