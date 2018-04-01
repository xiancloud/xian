package info.xiancloud.httpserver.core.monitor;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
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
        return UnitMeta.create("监控本地业务网关缓存的session数量")
                .setBroadcast(UnitMeta.Broadcast.create().setSuccessDataOnly(true).setAsync(true));
    }

    @Override
    public Input getInput() {
        return new Input();
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        return UnitResponse.createSuccess(HttpSessionLocalCache.getSessionMap().size());
    }
}
