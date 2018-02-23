package info.xiancloud.plugin.distribution.exception;

import info.xiancloud.plugin.distribution.service_discovery.GroupInstanceIdBean;

/**
 * @author happyyangyuan
 */
public class GroupInstanceOfflineException extends GroupOfflineException {
    private String serviceInstanceId;

    public GroupInstanceOfflineException(String serviceInstanceId) {
        super(new GroupInstanceIdBean(serviceInstanceId).getGroup());
        this.serviceInstanceId = serviceInstanceId;
    }

    @Override
    public String getMessage() {
        return "服务不在线：" + serviceInstanceId;
    }
}
