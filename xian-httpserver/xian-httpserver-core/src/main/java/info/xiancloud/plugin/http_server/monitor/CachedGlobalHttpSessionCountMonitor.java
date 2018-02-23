package info.xiancloud.plugin.http_server.monitor;

import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.UnitMeta;
import info.xiancloud.plugin.distribution.exception.UnitOfflineException;
import info.xiancloud.plugin.distribution.exception.UnitUndefinedException;
import info.xiancloud.plugin.distribution.loadbalance.UnitRouter;
import info.xiancloud.plugin.distribution.service_discovery.UnitInstance;
import info.xiancloud.plugin.message.SyncXian;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.support.falcon.AbstractDiyMonitorUnit;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.math.MathUtil;

import java.util.List;

/**
 * @author happyyangyuan
 */
public class CachedGlobalHttpSessionCountMonitor extends AbstractDiyMonitorUnit {
    @Override
    public String getName() {
        return "cachedGlobalHttpSessionCountMonitor";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("查询全局所有缓存的http session数量").setPublic(true);
    }

    @Override
    public Input getInput() {
        return new Input();
    }

    @Override
    public String title() {
        return "堆积情况";
    }

    @Override
    public Object execute0() {
        int totalSessionCount = 0;
        try {
            for (UnitInstance clientInfo : UnitRouter.singleton.allInstances(Unit.fullName("httpServer", "cachedLocalHttpSessionMonitor"))) {
                int localSessionCount;
                UnitRequest request = UnitRequest.create("httpServer", "cachedLocalHttpSessionMonitor");
                request.getContext().setDestinationNodeId(clientInfo.getNodeId());
                List<Integer> counts = SyncXian.call(request, 5 * 1000).dataToTypedList(Integer.class);
                totalSessionCount = MathUtil.sum(counts);
            }
        } catch (UnitOfflineException | UnitUndefinedException e) {
            LOG.error(e);
            totalSessionCount = -1;
        }
        LOG.info("当前缓存的session总数量为:" + totalSessionCount);
        return UnitResponse.success(totalSessionCount);
    }
}
