package info.xiancloud.httpserver.core.monitor;

import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.httpserver.core.unit.HttpServerGroup;
import info.xiancloud.httpserver.core.unit.HttpSessionLocalCache;

/**
 * 已经改造为broadcast型的unit
 *
 * @author happyyangyuan
 */
public class CachedLocalHttpSessionMonitor implements Unit {
    @Override
    public String getName() {
        return "cachedLocalHttpSessionMonitor";
    }

    @Override
    public Group getGroup() {
        return HttpServerGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("监控本地业务网关缓存的session数量")
                .setBroadcast(UnitMeta.Broadcast.create().setSuccessDataOnly(true)/*.setAsync(false)*/);
    }

    @Override
    public Input getInput() {
        return new Input();
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        handler.handle(UnitResponse.createSuccess(HttpSessionLocalCache.getSessionMap().size()));
    }
}
