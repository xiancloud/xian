package info.xiancloud.core.distribution.exception;

import info.xiancloud.core.Group;

/**
 * group undefined exception.
 *
 * @author happyyangyuan
 */
public class GroupUndefinedException extends AbstractXianException {
    private String group;

    public GroupUndefinedException(String group) {
        this.group = group;
    }

    @Override
    public String getMessage() {
        return "undefined group '".concat(group).concat("'");
    }

    @Override
    public String getCode() {
        return Group.CODE_GROUP_UNDEFINED;
    }

}
