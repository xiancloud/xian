package info.xiancloud.core.distribution.exception;

import info.xiancloud.core.distribution.service_discovery.UnitInstanceIdBean;
import info.xiancloud.core.distribution.service_discovery.UnitInstanceIdBean;

/**
 * @author happyyangyuan
 */
public class UnitInstanceOfflineException extends UnitOfflineException {

    private String unitInstanceId;

    public UnitInstanceOfflineException(String unitInstanceId) {
        super(new UnitInstanceIdBean(unitInstanceId).getFullName());
        this.unitInstanceId = unitInstanceId;
    }

    @Override
    public String getMessage() {
        return "unit实例不在线：" + unitInstanceId;
    }
}
