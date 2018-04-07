package info.xiancloud.plugin.monitor.common;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.distribution.exception.GroupOfflineException;
import info.xiancloud.core.distribution.exception.GroupUndefinedException;
import info.xiancloud.core.distribution.loadbalance.GroupRouter;
import info.xiancloud.core.distribution.service_discovery.GroupInstance;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.falcon.DiyMonitorGroup;
import info.xiancloud.core.util.LOG;

import java.util.HashSet;
import java.util.Set;

/**
 * @author happyyangyuan
 */
public class FactorCollector {

    private static final float ERROR_VALUE_NEGATIVE_1 = -1.0f;

    public static JSONObject collect() {
        JSONObject factorOriented = new JSONObject();
        String diyMonitorServiceName = new DiyMonitorGroup().getName();
        Set<String> diyMonitorUnitNames = new HashSet<>();
        try {
            for (GroupInstance serviceInstance : GroupRouter.singleton.allInstances(diyMonitorServiceName)) {
                diyMonitorUnitNames.addAll(serviceInstance.getPayload().getUnitNames());
            }
        } catch (GroupOfflineException | GroupUndefinedException e) {
            throw new RuntimeException(e);
        }
        for (String diyMonitorUnitName : diyMonitorUnitNames) {
            UnitResponse o = SingleRxXian.call(diyMonitorServiceName, diyMonitorUnitName).blockingGet();
            if (o.succeeded()) {
                try {
                    LOG.debug("data可以是单纯的数字，也可以是json/jsonArray");
                    factorOriented.put(diyMonitorUnitName, o.getData());
                } catch (Throwable e) {
                    LOG.error("收集指标 '" + diyMonitorUnitName + "' 时出现异常,返回-1作为指标值", e);
                    factorOriented.put(diyMonitorUnitName, ERROR_VALUE_NEGATIVE_1);
                }
            } else {
                LOG.error("收集指标 '" + diyMonitorUnitName + "' 失败 !  返回-1作为指标值   .   失败内容为 :" + o);
                factorOriented.put(diyMonitorUnitName, ERROR_VALUE_NEGATIVE_1);
            }
        }
        return factorOriented;
    }

}

