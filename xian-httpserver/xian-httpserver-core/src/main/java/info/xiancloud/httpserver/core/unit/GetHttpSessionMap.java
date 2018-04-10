package info.xiancloud.httpserver.core.unit;

import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;

/**
 * @author happyyangyuan
 */
public class GetHttpSessionMap implements Unit {
    @Override
    public Input getInput() {
        return null;
    }

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
        return UnitMeta.createWithDescription("获取业务网关内当前未处理的sessionMap内容")
                .setDocApi(false)
                .setBroadcast(UnitMeta.Broadcast.create().setSuccessDataOnly(true));
    }

    @Override
    public void execute(UnitRequest request, Handler<UnitResponse> handler) {
        handler.handle(UnitResponse.createSuccess(HttpSessionLocalCache.getSessionMap()));
    }

}
