package info.xiancloud.core.distribution.exception;

import info.xiancloud.core.Group;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author happyyangyuan
 */
public class ApplicationUndefinedException extends AbstractXianException {
    private Set<String> applications = new HashSet<>();

    public ApplicationUndefinedException(String application) {
        applications.add(application);
    }

    public ApplicationUndefinedException(Collection<String> applications) {
        this.applications.addAll(applications);
    }

    @Override
    public String getCode() {
        return Group.CODE_APPLICATION_UNDEFINED;
    }

    @Override
    public String getMessage() {
        return "application未定义：" + applications;
    }
}
