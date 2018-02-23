package info.xiancloud.plugin.http_server.monitor;

import info.xiancloud.plugin.*;
import info.xiancloud.plugin.http_server.unit.HttpServerGroup;
import info.xiancloud.plugin.http_server.unit.HttpSessionLocalCache;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;

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
        return UnitMeta.create("监控本地业务网关缓存的session数量")
                .setBroadcast(UnitMeta.Broadcast.create().setSuccessDataOnly(true).setAsync(true));
    }

    @Override
    public Input getInput() {
        return new Input();
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        return UnitResponse.success(HttpSessionLocalCache.getSessionMap().size());
    }
}
