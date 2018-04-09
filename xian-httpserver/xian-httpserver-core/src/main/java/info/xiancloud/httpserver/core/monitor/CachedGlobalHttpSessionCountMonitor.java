package info.xiancloud.httpserver.core.monitor;

import info.xiancloud.core.Input;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.falcon.AbstractDiyMonitorUnit;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.math.MathUtil;
import io.reactivex.Single;

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
        return UnitMeta.createWithDescription("查询全局所有缓存的http session数量").setDocApi(true);
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
    public Single<Object> execute0() {
        return SingleRxXian.call(UnitRequest.create("httpServer", "cachedLocalHttpSessionMonitor"))
                .map(unitResponse -> {
                    List<Integer> counts = unitResponse.dataToTypedList(Integer.class);
                    int totalSessionCount = MathUtil.sum(counts);
                    LOG.info("当前缓存的session总数量为:" + totalSessionCount);
                    return UnitResponse.createSuccess(totalSessionCount);
                });
    }
}
