package info.xiancloud.plugin.distribution.exception;

import info.xiancloud.plugin.Group;

import java.util.Collection;
import java.util.HashSet;

/**
 * 节点不可达
 *
 * @author happyyangyuan
 */
public class ApplicationOfflineException extends AbstractXianException {
    private Collection<String> applications;

    /**
     * 构造一个表示一批app都不在线的异常实例
     *
     * @param applications
     */
    public ApplicationOfflineException(Collection<String> applications) {
        this.applications = applications;
    }

    /**
     * 构造一个指定的app不在线的异常实例
     *
     * @param application
     */
    public ApplicationOfflineException(String application) {
        this.applications = new HashSet<>();
        applications.add(application);
    }

    @Override
    public String getCode() {
        return Group.CODE_APPLICATION_OFFLINE;
    }

    @Override
    public String getMessage() {
        return "目标节点不可达:" + applications;
    }


    public Collection<String> getApplications() {
        return applications;
    }
}
